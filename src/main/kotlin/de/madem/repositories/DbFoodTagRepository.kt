package de.madem.repositories

import de.madem.model.DBFoodTag
import de.madem.model.api.Additive
import de.madem.model.database.DBFoodTagTable
import de.madem.util.exceptions.DataAlreadyExistingException
import io.ktor.features.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf

class DbFoodTagRepository(private val database: Database) {
    fun add(foodTag: Additive) : RepositoryResponse<Int, Throwable>{
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