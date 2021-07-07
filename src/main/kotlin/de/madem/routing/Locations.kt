package de.madem.routing

import io.ktor.locations.Location

@Location ("/user/register") object UserRegisterRoute
@Location ("/user/login") object UserLoginRoute
@Location ("/user/logintest") object UserLoginTestRoute


@Location ("/restaurant/register") object RestaurantRegisterRouter
@Location("/restaurant") object AllRestaurantsRoute
@Location("/restaurant/{id}") object RestaurantByIdRoute
@Location("restaurant/{location}&{radius}") object RestaurantByLocationAndRadiusRoute

@Location("dish/all/{restaurant_id}") data class DishByRestaurantRoute(val restaurant_id: Int)
@Location("dish/{id}") data class DishByIdRoute(val id: Int)
@Location("dish/favourites/{user_id}") data class DishFavouritesByUserId(val user_id: Int)
@Location("dish/favourites/{id}&{user_id}") data class UserFavouriteDishes(val id: Int, val user_id: Int)