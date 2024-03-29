package de.madem.routing

import de.madem.model.api.NullableRestaurantInfo
import de.madem.model.api.Restaurant
import de.madem.modules.AppModule
import de.madem.repositories.RepositoryResponse
import de.madem.util.validation.RestaurantInfoValidator
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing

fun Routing.configureRestaurantRouting() {
    post<RestaurantRegisterRoute>{
        val registrationNullable = try{
            call.receive<NullableRestaurantInfo>()
        }
        catch (ex: Exception){
            ex.printStackTrace()
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val validator = RestaurantInfoValidator()
        if(!validator.isValid(registrationNullable)){
            System.err.println("Tried to register restaurant. Restaurant was not valid!")
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val registration = registrationNullable.notNullable()
        val addRestaurantResponse = AppModule.databaseRepository.restaurants.add(registration)
        when(addRestaurantResponse){
            is RepositoryResponse.Data -> call.respond(HttpStatusCode.OK)
            is RepositoryResponse.Error -> {
                //TODO: handle more errors
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }

    get<AllRestaurantsRoute>{
        when(val dbResponse = AppModule.databaseRepository.restaurants.getAll()) {
            is RepositoryResponse.Data -> call.respond(dbResponse.value)
            is RepositoryResponse.Error -> call.respond(HttpStatusCode.NotFound)
        }
    }

    get<RestaurantByIdRoute>{
        val id = it.id //call.request.queryParameters["id"]?.toInt()
        if(id != null) {
            when(val dbResponse = AppModule.databaseRepository.restaurants.getRestaurantById(id)) {
                is RepositoryResponse.Data -> with(dbResponse.value){
                    call.respond(Restaurant(title,details,pictureURL,phone ?: ""))
                }
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.NotFound)
            }
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    get<RestaurantByLocationAndRadiusRoute>{ route ->
        println("Location is: ${route.location}\nRadius is: ${route.radius}")
        val location = route.location?.removeSurrounding("[", "]")?.split(",")?.map { it.toDoubleOrNull()} //call.request.queryParameters["location"]?.removeSurrounding("[", "]")?.split(",")?.map { it.toDouble() }
        val radius = route.radius?.toIntOrNull() //call.request.queryParameters["radius"]?.toInt()

        if(location != null
            && location.all { it != null }
            && location.size == 2
            && radius != null
            && radius >= 0) {
            when (val dbResponse =
                AppModule.databaseRepository.restaurants.getRestaurantByLocationAndRadius(location.map { it!! }, radius)) {
                is RepositoryResponse.Data -> {
                    val responseData = dbResponse
                        .value
                        .map { Restaurant(it.title,it.details,it.pictureURL, it.phone ?: "") }
                    call.respond(responseData)
                }
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.NotFound)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

}