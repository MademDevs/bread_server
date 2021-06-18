package de.madem.routing

import io.ktor.locations.Location

@Location ("/user/register") object UserRegisterRoute
@Location ("/user/login") object UserLoginRoute
@Location ("/user/logintest") object UserLoginTestRoute


@Location ("/restaurant/register") object RestaurantRegisterRouter
@Location("/restaurant") object AllRestaurantsRoute

