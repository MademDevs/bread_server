package de.madem.routing

import de.madem.model.api.AccountRegistration
import de.madem.repositories.DatabaseRepository
import de.madem.repositories.RepositoryResponse
import de.madem.util.validation.AccountRegistrationValidator
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*

@KtorExperimentalLocationsAPI
fun Routing.configureUserRouting(){
    post<UserRegisterRoute> {
        val registration = call.receive<AccountRegistration>()
        val validator = AccountRegistrationValidator()
        if (!validator.isValid(registration)) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val dbResponse = DatabaseRepository.users.add(registration)
        when(dbResponse) {
            is RepositoryResponse.Error -> call.respond(HttpStatusCode.Conflict)
            is RepositoryResponse.Data -> call.respondText { "Registration successful. Welcome ${registration.username}! " }
        }
    }
}