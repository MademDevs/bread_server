package de.madem.repositories

import de.madem.model.DBRestaurant
import de.madem.model.api.RestaurantInfo
import de.madem.model.database.DBRestaurantTable
import jdk.jshell.spi.ExecutionControl
import org.ktorm.database.Database
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

class DbRestaurantRepository(private val database: Database, private val dbRepo: DatabaseRepository) {
    //#region Functions
    fun getAll(): RepositoryResponse<List<DBRestaurant>,Throwable>{
        val data = database.sequenceOf(DBRestaurantTable).toList()
        return RepositoryResponse.Data(data)
    }

    fun add(registration: RestaurantInfo) : RepositoryResponse<Int,Throwable>{
        val addressResponse = dbRepo
            .addresses
            .merge(registration.address, shouldUpdateExisting = false)

        val addressId = (addressResponse as? RepositoryResponse.Data)?.value
            ?: return addressResponse as RepositoryResponse.Error

        val genRestaurantId = database
            .insertAndGenerateKey(DBRestaurantTable){
                set(it.title, registration.restaurant.title)
                set(it.details, registration.restaurant.details)
                set(it.phone, registration.restaurant.phone)
                set(it.pictureURL, registration.restaurant.pictureURL)
                set(it.addressID, addressId)
            }


        return RepositoryResponse.Error(ExecutionControl.NotImplementedException("Implement add for Restaurant"))
    }
    //#endregion
}