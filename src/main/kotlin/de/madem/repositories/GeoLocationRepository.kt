package de.madem.repositories

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.Result
import de.madem.model.api.Address
import de.madem.model.api.GeoLocationApiModel

class GeoLocationRepository {

    fun getGeoLocation(address: Address): Pair<Double?, Double?> {
        val uri = "https://geocode.xyz/${address.street}+${address.number}+${address.zipCode}+${address.city}?json=1"
        val (_, _, result) = FuelManager().get(uri).responseObject<GeoLocationApiModel>()
        return when(result) {
            is Result.Failure -> Pair(null, null)
            is Result.Success -> result.value.getLongLat()
        }
    }

}