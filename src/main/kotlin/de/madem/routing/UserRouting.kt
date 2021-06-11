package de.madem.routing

import com.google.gson.stream.MalformedJsonException
import de.madem.model.api.NullableAccountLoginRequest
import de.madem.model.api.AccountLoginResponse
import de.madem.model.api.NullableAccountRegistration
import de.madem.modules.AppModule
import de.madem.repositories.RepositoryResponse
import de.madem.util.security.JwtConfig
import de.madem.util.validation.AccountLoginRequestValidator
import de.madem.util.validation.AccountRegistrationValidator
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*

fun Routing.configureUserRouting(){
    post<UserRegisterRoute> {
        val registrationNullable = try{
            call.receive<NullableAccountRegistration>()
        }
        catch (ex: Exception){
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }


        val validator = AccountRegistrationValidator()
        if (!validator.isValid(registrationNullable)) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val dbResponse = AppModule.databaseRepository.users.add(registrationNullable.notNullable())
        when(dbResponse) {
            is RepositoryResponse.Error -> call.respond(HttpStatusCode.Conflict)
            is RepositoryResponse.Data -> call.respond(HttpStatusCode.OK)
        }
    }

    post<UserLoginRoute>{
        val loginRequestNullable = try {
            call.receive<NullableAccountLoginRequest>()
        }
        catch (ex: Exception){
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val validator = AccountLoginRequestValidator()
        if (!validator.isValid(loginRequestNullable)) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val loginRequest = loginRequestNullable.notNullable()
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