package de.madem.modules

import de.madem.repositories.DatabaseRepository
import de.madem.repositories.GeoLocationRepository
import de.madem.util.security.JwtConfig
import de.madem.util.security.system.SystemProperties

object AppModule {
    private val systemProperties = SystemProperties()
    val databaseRepository = DatabaseRepository(systemProperties)
    val jwtConfig = JwtConfig(systemProperties.jwtsecret ?: "no_jwt_secret")
    val geoRepository = GeoLocationRepository()
}