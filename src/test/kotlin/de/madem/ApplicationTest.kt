package de.madem

import de.madem.model.api.Address
import de.madem.repositories.GeoLocationRepository
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
        }
    }
    @Test
    fun testGeoAPI() {
        val repo = GeoLocationRepository()
        val address = Address(
            street = "Parkstr.",
            number = "6",
            zipCode = "95213",
            city = "Münchberg",
            country = "Deutschland"
        )
        val location = repo.getGeoLocation(address)
        print(location)
    }

}