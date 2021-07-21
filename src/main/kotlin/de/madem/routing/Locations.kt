package de.madem.routing

import io.ktor.locations.Location

@Location ("/user/register") object UserRegisterRoute
@Location ("/user/login") object UserLoginRoute
@Location ("/user/logintest") object UserLoginTestRoute


@Location ("/restaurant/register") object RestaurantRegisterRoute
@Location("/restaurant") object AllRestaurantsRoute
@Location("/restaurant/{id}") data class RestaurantByIdRoute(val id : Int)
//@Location("restaurant/{location}&{radius}") data class RestaurantByLocationAndRadiusRoute(val location: String, val radius: String)
@Location("/restaurant/find") data class RestaurantByLocationAndRadiusRoute(val location: String?, val radius: String?)

@Location("dish/all/{restaurant_id}") data class DishByRestaurantRoute(val restaurant_id: Int)
@Location("dish/{id}") data class DishByIdRoute(val id: Int)
@Location("dish/favourites/{user_id}") data class DishFavouritesByUserId(val user_id: Int)
@Location("dish/favourites/{user_id}") data class UserFavouriteDishes(val user_id: Int)

@Location("/additive/{userId}") data class AdditiveUserRoute(val userId: Int)
@Location("/additive") object AdditiveRoute

@Location("/foodtag/{userId}") data class FoodTagUserRoute(val userId: Int)
@Location("/foodtag") object FoodTagRoute

