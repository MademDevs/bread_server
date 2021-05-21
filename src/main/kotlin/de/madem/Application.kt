package de.madem

import de.madem.repositories.DatabaseRepository
import de.madem.routing.configureRouting
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.util.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.application.*
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.response.*
import io.ktor.request.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

/**
 * Please note that you can use any other name instead of *module*.
 * Also note that you can have more then one modules in your application.
 * */
@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
@KtorExperimentalAPI
fun Application.module(testing: Boolean = false) {
    installFeatures()
    configureRouting()
}

private fun Application.installFeatures() {
    install(ContentNegotiation) {
        gson {
        }
    }
    install(Locations)
}
