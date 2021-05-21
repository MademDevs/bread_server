package de.madem.routing

import de.madem.repositories.DatabaseRepository
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

@KtorExperimentalLocationsAPI
fun Application.configureRouting() = routing {

    //demo Routing
    get("/") {
        println(DatabaseRepository.test().size)
        call.respondText("Hello World from Bread!")
    }
    get("/json/gson") {
        call.respond(mapOf("hello" to "world"))
    }

    // configure user routes
    configureUserRouting()
}