package de.madem.routing

import de.madem.modules.AppModule
import de.madem.repositories.RepositoryResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.routing.get

fun Routing.configureDishRouting() {
    get<DishByRestaurantRoute>{
        val restaurantId = it.restaurant_id
        if(restaurantId != null) {
            when(val dbResponse = AppModule.databaseRepository.dishes.getDishesOfRestaurant(restaurantId)) {
                is RepositoryResponse.Data -> call.respond(dbResponse.value)
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.NotFound)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get<DishByIdRoute>{
        val dishId = it.id
        if (dishId != null){
            when(val dbResponse = AppModule.databaseRepository.dishes.getDishById(dishId)) {
                is RepositoryResponse.Data -> call.respond(dbResponse.value)
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.NotFound)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    get<DishFavouritesByUserId>{
        val userId = it.user_id
        if(userId != null) {
            when(val dbResponse = AppModule.databaseRepository.dishes.getDishFavouritesByUserId(userId)) {
                is RepositoryResponse.Data -> call.respond(dbResponse.value)
                is RepositoryResponse.Error -> call.respond(HttpStatusCode.NotFound)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    patch<UserFavouriteDishes>{
        val userId = it.user_id
        val dishId = try {
            call.receive<Int>()
        } catch(ex : Exception){
            call.respond(HttpStatusCode.BadRequest)
            return@patch
        }

        if(userId != null && dishId != null){
            when(val dbResponse = AppModule.databaseRepository.dishes.updateUserFavourites(userId, dishId)) {
                is RepositoryResponse.Data -> call.respond(dbResponse.value)
                is RepositoryResponse.Error -> call.respond(dbResponse.error)
            }
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}