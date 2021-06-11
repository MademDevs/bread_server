package de.madem.routing

import de.madem.modules.AppModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.configureRouting() = routing {
    //demo Routing
    get("/") {
        println(AppModule.databaseRepository.test().size)
        call.respondText("Hello World from Bread!")
    }
    get("/json/gson") {
        call.respond(mapOf("hello" to "world"))
    }

    // configure user routes
    configureUserRouting()

    configureRestaurantRouting()
}