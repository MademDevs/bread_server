package de.madem.repositories

import de.madem.model.DBAdditive
import de.madem.model.api.Additive
import de.madem.model.database.DBAdditiveTable
import de.madem.model.database.DBUserAllergicToAdditiveTable
import de.madem.util.exceptions.DataAlreadyExistingException
import io.ktor.features.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.support.mysql.bulkInsert

class DbAdditiveRepository(private val database: Database) {
    fun add(additive: Additive) : RepositoryResponse<Int, Throwable>{
        val alreadyExisting = getAdditiveByTitle(additive.title) is RepositoryResponse.Data
        return if(alreadyExisting) {
            RepositoryResponse.Error(DataAlreadyExistingException(additive))
        }
        else{
            val genId = database.insertAndGenerateKey(DBAdditiveTable){
                set(it.title, additive.title)
            }
            return RepositoryResponse.Data(genId as Int)
        }
    }

    fun mergeAdditive(additive: Additive, shouldUpdateExisting : Boolean = false) : RepositoryResponse<Int,Throwable>{
        val existingResponse = getAdditiveByTitle(additive.title)
        when(existingResponse){
            is RepositoryResponse.Data -> {
                if(shouldUpdateExisting){
                    val updateResponse = updateAdditiveById(existingResponse.value.id, additive)
                    when(updateResponse){
                        is RepositoryResponse.Error -> return updateResponse
                    }
                }

                return RepositoryResponse.Data(existingResponse.value.id)
            }
            is RepositoryResponse.Error -> {
                return add(additive)
            }
        }
    }

    fun updateAdditiveById(id: Int, additive: Additive) : RepositoryResponse<Boolean, Throwable>{
        val modCnt = database
            .update(DBAdditiveTable){
                set(it.title, additive.title)
                where {
                    (it.id eq id)
                }
            }

        return if(modCnt > 0){
            RepositoryResponse.Data(true)
        }
        else{
            RepositoryResponse.Error("Update failed!")
        }
    }

    fun getAdditiveByTitle(title: String): RepositoryResponse<DBAdditive, Throwable>{
        val fetchedByTitle = database.sequenceOf(DBAdditiveTable).firstOrNull { it.title eq title }
        return if(fetchedByTitle == null){
            RepositoryResponse.Error(NotFoundException())
        }
        else{
            RepositoryResponse.Data(fetchedByTitle)
        }
    }

    fun getAdditiveById(id: Int) : RepositoryResponse<DBAdditive,Throwable>{
        val fetchedById = database
            .sequenceOf(DBAdditiveTable)
            .firstOrNull { it.id eq id }
            ?: return RepositoryResponse.Error(NotFoundException())
        return RepositoryResponse.Data(fetchedById)
    }

    fun setAdditivesOfUserByIds(
        userId : Int,
        newAdditiveIds : List<Int>
    ) : RepositoryResponse<Boolean, Throwable> {
        val containsUnknownAdditive = newAdditiveIds.any {
            getAdditiveById(it) is RepositoryResponse.Error
        }

        if(containsUnknownAdditive){
            //return Not Found to indicate that some additives are unknown
            return RepositoryResponse.Error(NotFoundException())
        }

        //set user additives
        val additivesOfUserResponse = getAdditivesOfUser(userId)
        val additiveIdsOfUser = when(additivesOfUserResponse){
            is RepositoryResponse.Error -> return additivesOfUserResponse
            is RepositoryResponse.Data -> additivesOfUserResponse.value.map { it.id }
        }

        //determine, which additives to delete and which to add
        val addElements = newAdditiveIds.filter { !additiveIdsOfUser.contains(it) }
        val deleteElements = additiveIdsOfUser.filter { !newAdditiveIds.contains(it) }

        deleteElements.forEach { deleteAdditiveId ->
            database.delete(DBUserAllergicToAdditiveTable){
                it.userID eq userId
                it.additiveID eq deleteAdditiveId
            }
        }

        addElements.forEach {addAdditiveId ->
            database.insert(DBUserAllergicToAdditiveTable){
                set(it.additiveID,addAdditiveId)
                set(it.userID, userId)
            }
        }


        return RepositoryResponse.Data(true)
    }

    fun getAdditivesOfUser(userId: Int) : RepositoryResponse<List<DBAdditive>,Throwable> {
        val additivesOfUser = database
            .sequenceOf(DBUserAllergicToAdditiveTable)
            .filter { it.userID eq userId }
            .map { it.additive }
            .toList()

        return RepositoryResponse.Data(additivesOfUser)
    }
}