package de.madem.routing

import de.madem.model.api.FoodTagWithId
import de.madem.modules.AppModule
import de.madem.repositories.RepositoryResponse
import de.madem.util.security.JwtConfig
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

fun Routing.configureFoodTagRouting(){
    authenticate {
        patch<FoodTagUserRoute> {

            //auth
            val authSuc = authenticateJwtUserWithUrlId(it.userId)
            if (!authSuc){
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
                .foodTags
                .setFoodTagsOfUserByIds(it.userId,ids)

            if(repositoryResponse is RepositoryResponse.Error){
                val responseStatusCode = when(repositoryResponse.error){
                    is NotFoundException -> HttpStatusCode.NotFound
                    else -> HttpStatusCode.InternalServerError
                }
                call.respond(responseStatusCode)
                return@patch
            }

            val getAllFoodTagsOfUserResponse = AppModule
                .databaseRepository
                .foodTags
                .getFoodTagsOfUser(it.userId)

            when(getAllFoodTagsOfUserResponse){
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.InternalServerError)
                is RepositoryResponse.Data -> call.respond(
                    getAllFoodTagsOfUserResponse
                        .value
                        .map { dbFT -> FoodTagWithId(dbFT.id,dbFT.title) }
                )
            }
        }

        get<FoodTagUserRoute>{
            //auth
            val authSuccess = authenticateJwtUserWithUrlId(it.userId)
            if (!authSuccess){
                return@get
            }

            //get impl
            val getAllFoodTagsOfUserResponse = AppModule
                .databaseRepository
                .foodTags
                .getFoodTagsOfUser(it.userId)

            when(getAllFoodTagsOfUserResponse){
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.InternalServerError)
                is RepositoryResponse.Data -> call.respond(
                    getAllFoodTagsOfUserResponse
                        .value
                        .map { dbFT -> FoodTagWithId(dbFT.id,dbFT.title) }
                )
            }
        }

        get<FoodTagRoute>{
            with(call){
                //auth
                val jwtUser = authentication.principal as? JwtConfig.JwtUser
                if(jwtUser == null){
                    respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                when(val getAllResponse = AppModule.databaseRepository.foodTags.getAllFoodTags()){
                    is RepositoryResponse.Error -> respond(HttpStatusCode.InternalServerError)
                    is RepositoryResponse.Data -> {
                        respond(getAllResponse.value.map { FoodTagWithId(it.id, it.title) })
                    }
                }
            }
        }
    }
}