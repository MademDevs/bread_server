package de.madem.routing

import de.madem.model.api.AdditiveWithId
import de.madem.modules.AppModule
import de.madem.repositories.RepositoryResponse
import de.madem.util.security.JwtConfig
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.patch
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.util.pipeline.PipelineContext

fun Routing.configureAdditiveRouting(){


    authenticate {

        patch<AdditiveUserRoute> {

            //auth
            val authSuccess = authenticateJwtUserWithUrlId(it.userId)
            if (!authSuccess){
                return@patch
            }


            val ids = try {
                call.receive<IntArray>().toList()
            } catch(ex : Exception){
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }

            val repositoryResponse = AppModule
                .databaseRepository
                .additives
                .setAdditivesOfUserByIds(it.userId,ids)

            if(repositoryResponse is RepositoryResponse.Error){
                val responseStatusCode = when(repositoryResponse.error){
                    is NotFoundException -> HttpStatusCode.NotFound
                    else -> HttpStatusCode.InternalServerError
                }
                call.respond(responseStatusCode)
                return@patch
            }

            val getAllAdditivesOfUserResponse = AppModule
                .databaseRepository
                .additives
                .getAdditivesOfUser(it.userId)

            when(getAllAdditivesOfUserResponse){
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.InternalServerError)
                is RepositoryResponse.Data -> call.respond(
                    getAllAdditivesOfUserResponse
                        .value
                        .map { dbAdd -> AdditiveWithId(dbAdd.id,dbAdd.title) }
                )
            }
        }

        get<AdditiveUserRoute>{
            //auth
            val authSuccess = authenticateJwtUserWithUrlId(it.userId)
            if (!authSuccess){
                return@get
            }

            //get impl
            val getAllAdditivesOfUserResponse = AppModule
                .databaseRepository
                .additives
                .getAdditivesOfUser(it.userId)

            when(getAllAdditivesOfUserResponse){
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.InternalServerError)
                is RepositoryResponse.Data -> call.respond(
                    getAllAdditivesOfUserResponse
                        .value
                        .map { dbAdd -> AdditiveWithId(dbAdd.id,dbAdd.title) }
                )
            }
        }
    }
}


private suspend fun PipelineContext<Unit, ApplicationCall>.authenticateJwtUserWithUrlId(urlUserId: Int) : Boolean{
    val authUserId = (call.authentication.principal as? JwtConfig.JwtUser)?.userId
    if(authUserId == null){
        call.respond(HttpStatusCode.Unauthorized)
        return false
    }
    else if (authUserId != urlUserId){
        call.respond(HttpStatusCode.Forbidden)
        return false
    }

    return true
}