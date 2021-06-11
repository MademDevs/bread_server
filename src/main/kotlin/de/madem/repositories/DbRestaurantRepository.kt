package de.madem.repositories

import de.madem.model.DBRestaurant
import de.madem.model.api.RestaurantInfo
import de.madem.model.database.DBRestaurantTable
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

class DbRestaurantRepository(private val database: Database, private val dbRepo: DatabaseRepository) {
    //#region Functions
    fun getAll(): RepositoryResponse<List<DBRestaurant>,Throwable>{
        val data = database.sequenceOf(DBRestaurantTable).toList()
        return RepositoryResponse.Data(data)
    }

    /*fun add(registration: RestaurantInfo) : RepositoryResponse<Int,Throwable>{

    }*/
    //#endregion
}