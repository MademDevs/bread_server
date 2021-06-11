package de.madem.repositories

import de.madem.model.DBFoodTag
import de.madem.model.api.Additive
import de.madem.model.api.FoodTag
import de.madem.model.database.DBFoodTagTable
import de.madem.util.exceptions.DataAlreadyExistingException
import io.ktor.features.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf

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
}