package de.madem.repositories

import de.madem.model.DBFoodTag
import de.madem.model.api.FoodTag
import de.madem.model.database.DBFoodTagTable
import de.madem.model.database.DBUserLikesFoodTagTable
import de.madem.util.exceptions.DataAlreadyExistingException
import io.ktor.features.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.*
import org.ktorm.support.mysql.bulkInsert

class DbFoodTagRepository(private val database: Database) {
    fun add(foodTag: FoodTag) : RepositoryResponse<Int, Throwable>{
        val alreadyExisting = getFoodTagByTitle(foodTag.title) is RepositoryResponse.Data
        return if(alreadyExisting) {
            RepositoryResponse.Error(DataAlreadyExistingException(foodTag))
        }
        else{
            val genId = database.insertAndGenerateKey(DBFoodTagTable){
                set(it.title, foodTag.title)
            }
            return RepositoryResponse.Data(genId as Int)
        }
    }

    fun mergeFoodTag(foodTag: FoodTag, shouldUpdateExisting : Boolean = false) : RepositoryResponse<Int,Throwable>{
        val existingResponse = getFoodTagByTitle(foodTag.title)
        when(existingResponse){
            is RepositoryResponse.Data -> {
                if(shouldUpdateExisting){
                    val updateResponse = updateFoodTagById(existingResponse.value.id, foodTag)
                    when(updateResponse){
                        is RepositoryResponse.Error -> return updateResponse
                    }
                }

                return RepositoryResponse.Data(existingResponse.value.id)
            }
            is RepositoryResponse.Error -> {
                return add(foodTag)
            }
        }
    }

    fun updateFoodTagById(id: Int, foodTag: FoodTag) : RepositoryResponse<Boolean, Throwable>{
        val modCnt = database
            .update(DBFoodTagTable){
                set(it.title, foodTag.title)
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

    fun getFoodTagByTitle(title: String): RepositoryResponse<DBFoodTag, Throwable>{
        val fetchedByTitle = database.sequenceOf(DBFoodTagTable).firstOrNull { it.title eq title }
        return if(fetchedByTitle == null){
            RepositoryResponse.Error(NotFoundException())
        }
        else{
            RepositoryResponse.Data(fetchedByTitle)
        }
    }

    fun getAllFoodTags() : RepositoryResponse<List<DBFoodTag>,Throwable>{
        val fetched = database.sequenceOf(DBFoodTagTable).toList()
        return RepositoryResponse.Data(fetched)
    }

    fun getFoodTagById(id: Int) : RepositoryResponse<DBFoodTag,Throwable>{
        val fetchedById = database
            .sequenceOf(DBFoodTagTable)
            .firstOrNull { it.id eq id }
            ?: return RepositoryResponse.Error(NotFoundException())
        return RepositoryResponse.Data(fetchedById)
    }

    fun setFoodTagsOfUserByIds(
        userId : Int,
        newFoodTagIds : List<Int>
    ) : RepositoryResponse<Boolean, Throwable> {
        val containsUnknownFoodTag = newFoodTagIds.any {
            getFoodTagById(it) is RepositoryResponse.Error
        }

        if(containsUnknownFoodTag){
            //return Not Found to indicate that some foodtags are unknown
            return RepositoryResponse.Error(NotFoundException())
        }

        //set user additives
        val foodtagsOfUserResponse = getFoodTagsOfUser(userId)
        val foodtagIdsOfUser = when(foodtagsOfUserResponse){
            is RepositoryResponse.Error -> return foodtagsOfUserResponse
            is RepositoryResponse.Data -> foodtagsOfUserResponse.value.map { it.id }
        }

        //determine, which additives to delete and which to add
        val addElements = newFoodTagIds.filter { !foodtagIdsOfUser.contains(it) }
        val deleteElements = foodtagIdsOfUser.filter { !newFoodTagIds.contains(it) }

        deleteElements.forEach { deleteFoodTagId ->
            database.delete(DBUserLikesFoodTagTable){
                it.userID eq userId
                it.foodTagID eq deleteFoodTagId
            }
        }

        if(addElements.isNotEmpty()){
            database.bulkInsert(DBUserLikesFoodTagTable){
                addElements.forEach {addFoodTagId ->
                    item {
                        set(it.foodTagID,addFoodTagId)
                        set(it.userID, userId)
                    }
                }
            }
        }

        return RepositoryResponse.Data(true)
    }

    fun getFoodTagsOfUser(userId: Int) : RepositoryResponse<List<DBFoodTag>,Throwable> {
        val foodTagsOfUser = database
            .sequenceOf(DBUserLikesFoodTagTable)
            .filter { it.userID eq userId }
            .map { it.foodTag }
            .toList()

        return RepositoryResponse.Data(foodTagsOfUser)
    }
}