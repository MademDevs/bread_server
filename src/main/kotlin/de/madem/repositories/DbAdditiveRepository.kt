package de.madem.repositories

import de.madem.model.DBAdditive
import de.madem.model.api.Additive
import de.madem.model.database.DBAdditiveTable
import de.madem.model.database.DBFoodTagTable
import de.madem.util.exceptions.DataAlreadyExistingException
import io.ktor.features.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf

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
}