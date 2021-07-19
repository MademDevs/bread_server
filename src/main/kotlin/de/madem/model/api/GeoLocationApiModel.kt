package de.madem.model.api

data class GeoLocationApiModel(
    val alt: Alt,
    val elevation: Elevation,
    val latt: String?,
    val longt: String?,
    val standard: Standard
){
    fun getLongLat(): Pair<Double?, Double?> {
        return Pair(longt?.toDoubleOrNull(), latt?.toDoubleOrNull())
    }
}

class Alt(
)

class Elevation(
)

data class Standard(
    val addresst: String,
    val city: String,
    val confidence: String,
    val countryname: String,
    val latt: String,
    val longt: String,
    val postal: String,
    val prov: String,
    val region: String,
    val stnumber: String
)