package de.madem.routing

import de.madem.model.api.AccountLoginRequest
import de.madem.model.api.AccountLoginResponse
import de.madem.model.api.AccountRegistration
import de.madem.modules.AppModule
import de.madem.repositories.RepositoryResponse
import de.madem.util.security.JwtConfig
import de.madem.util.validation.AccountRegistrationValidator
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
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

        val dbResponse = AppModule.databaseRepository.users.add(registration)
        when(dbResponse) {
            is RepositoryResponse.Error -> call.respond(HttpStatusCode.Conflict)
            is RepositoryResponse.Data -> call.respond(HttpStatusCode.OK)
        }
    }

    post<UserLoginRoute>{
        val loginRequest = call.receive<AccountLoginRequest>()
        val userResponse = AppModule.databaseRepository.users.getUserByUsername(loginRequest.username)

        when(userResponse){
            is RepositoryResponse.Error -> {
                when(userResponse.error) {
                    is NotFoundException -> call.respond(HttpStatusCode.NotFound, userResponse.error.localizedMessage)
                    else -> call.respond(HttpStatusCode.InternalServerError)
                }
            }
            is RepositoryResponse.Data -> {
                //auth user with password
                val isCorrectPassword = AppModule
                    .databaseRepository
                    .users
                    .isCorrectPassword(loginRequest.password,userResponse.value.password)

                if (!isCorrectPassword) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }

                val token = AppModule
                    .jwtConfig
                    .generateToken(JwtConfig.JwtUser(userResponse.value.id, userResponse.value.userName))
                call.respond(AccountLoginResponse(token, userResponse.value.id))
            }
        }
    }

    //TODO: Remove this test route later
    authenticate {
        get<UserLoginTestRoute>{
            (call.authentication.principal as? JwtConfig.JwtUser)?.let {
                call.respondText { "You are successfully logged in as user ${it.userName} with id ${it.userId}" }
            }
        }
    }
}