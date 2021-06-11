package de.madem.repositories

import de.madem.model.DBRestaurant
import de.madem.model.api.RestaurantInfo
import de.madem.model.database.DBRestaurantTable
import de.madem.modules.AppModule
import de.madem.util.exceptions.RestaurantAlreadyExistingException
import io.ktor.features.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.support.mysql.toLowerCase

class DbRestaurantRepository(private val database: Database) {
    //#region Functions
    fun getAll(): RepositoryResponse<List<DBRestaurant>,Throwable>{
        val data = database.sequenceOf(DBRestaurantTable).toList()
        return RepositoryResponse.Data(data)
    }

    fun add(registration: RestaurantInfo) : RepositoryResponse<Int,Throwable>{
        val addressResponse = AppModule.databaseRepository
            .addresses
            .merge(registration.address, shouldUpdateExisting = false)

        val addressId = (addressResponse as? RepositoryResponse.Data)?.value
            ?: return addressResponse as RepositoryResponse.Error

        val existing = getRestaurantByTitleAndAddressId(registration.restaurant.title, addressId) is RepositoryResponse.Data

        if(existing){
            return RepositoryResponse.Error(RestaurantAlreadyExistingException(registration.restaurant))
        }

        val genRestaurantId = database
            .insertAndGenerateKey(DBRestaurantTable){
                set(it.title, registration.restaurant.title)
                set(it.details, registration.restaurant.details)
                set(it.phone, registration.restaurant.phone)
                set(it.pictureURL, registration.restaurant.pictureURL)
                set(it.addressID, addressId)
            } as Int

        val dishMergeResponse = AppModule.databaseRepository.dishes.mergeDishesForRestaurant(
            registration.dishes,
            genRestaurantId
        )

        return when(dishMergeResponse){
            is RepositoryResponse.Data -> RepositoryResponse.Data(genRestaurantId)
            is RepositoryResponse.Error -> dishMergeResponse
        }
    }

    fun getRestaurantByTitleAndAddressId(title: String, addressId: Int) : RepositoryResponse<DBRestaurant, Throwable>{
        val fetched =database
            .sequenceOf(DBRestaurantTable)
            .firstOrNull {
                (it.title.toLowerCase() eq title.toLowerCase()) and (it.addressID eq addressId)
            } ?: return RepositoryResponse.Error(NotFoundException())
        return RepositoryResponse.Data(fetched)
    }
    //#endregion
}