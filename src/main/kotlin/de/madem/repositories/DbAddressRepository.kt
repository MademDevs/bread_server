package de.madem.repositories

import de.madem.model.DBAddress
import de.madem.model.api.Address
import de.madem.model.database.DBAddressTable
import de.madem.modules.AppModule
import de.madem.util.exceptions.InvalidIdException
import io.ktor.features.NotFoundException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import javax.management.Query.and

class DbAddressRepository(private val database: Database) {
    suspend fun merge(addressList: List<Address>, shouldUpdateExisting : Boolean = true) : RepositoryResponse<List<Int>,Throwable>{
        val idList = addressList.map {address ->
            val mergeResponse = merge(address, shouldUpdateExisting)
            when(mergeResponse){
                is RepositoryResponse.Error -> return@map null
                is RepositoryResponse.Data -> return@map mergeResponse.value
            }
        }.filterNotNull()

        return if(idList.size < addressList.size) {
            RepositoryResponse.Error("Some Data was not correctly merged!")
        }
        else {
            RepositoryResponse.Data(idList)
        }

    }

    suspend fun merge(address: Address, shouldUpdateExisting : Boolean = true) : RepositoryResponse<Int,Throwable> = coroutineScope{
        when(val existing = getAddressByAddressParameters(address)){
            is RepositoryResponse.Data -> {
                if(shouldUpdateExisting){
                    val updateResponse = updateAddressById(existing.value.id, address)
                    when(updateResponse){
                        is RepositoryResponse.Error -> return@coroutineScope updateResponse
                    }
                }
                return@coroutineScope RepositoryResponse.Data(existing.value.id)
            }

            is RepositoryResponse.Error -> with(address){
                val locationDef = async {
                    return@async AppModule.geoRepository.getGeoLocation(this@with)
                }
                var location = locationDef.await()

                if(location.first == null || location.second == null) {
                    location = Pair(11.941, 50.325)
                }

                val genId = database.insertAndGenerateKey(DBAddressTable){
                    set(it.street, street)
                    set(it.houseNumber, number)
                    set(it.zipCode,zipCode)
                    set(it.city,city)
                    set(it.country, country)
                    set(it.long, location.first)
                    set(it.lat, location.second)
                }
                return@coroutineScope RepositoryResponse.Data(genId as Int)
            }
        }
    }

    fun getAddressByAddressParameters(
        street: String,
        number: String,
        zipCode: String,
        city: String,
        country: String) : RepositoryResponse<DBAddress, Throwable>{
        val fetchedByParams = database
            .sequenceOf(DBAddressTable)
            .firstOrNull {
                (it.street eq street) and
                (it.houseNumber eq number) and
                (it.zipCode eq zipCode) and
                (it.city eq city) and
                (it.country eq country)
            } ?: return RepositoryResponse.Error(NotFoundException())
        return RepositoryResponse.Data(fetchedByParams)
    }

    fun getAddressByAddressParameters(address: Address) : RepositoryResponse<DBAddress, Throwable> = with(address){
        return getAddressByAddressParameters(street, number, zipCode, city, country)
    }

    fun updateAddressById(id: Int, address: Address): RepositoryResponse<Boolean, Throwable> {
        val updatedRowCnt = database.update(DBAddressTable){
            set(it.street, address.street)
            set(it.houseNumber, address.number)
            set(it.zipCode,address.zipCode)
            set(it.city,address.city)
            set(it.country, address.country)
            where {
                (it.id eq id)
            }
        }

        return if(updatedRowCnt > 0) RepositoryResponse.Data(true) else RepositoryResponse.Error("Update failed")
    }

    fun getAddressById(id: Int): RepositoryResponse<DBAddress, Throwable>{
        if(id < 0) {
            return RepositoryResponse.Error(InvalidIdException(id))
        }

        val fetchedById = database
            .sequenceOf(DBAddressTable)
            .firstOrNull {
                it.id eq id
            } ?: return RepositoryResponse.Error(NotFoundException())

        return RepositoryResponse.Data(fetchedById)
    }
}