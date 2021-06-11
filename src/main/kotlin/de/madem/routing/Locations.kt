package de.madem.routing

import io.ktor.locations.Location

@Location ("/register") object UserRegisterRoute
@Location ("/login") object UserLoginRoute
@Location ("/logintest") object UserLoginTestRoute


@Location ("/restaurant/register") object RestaurantRegisterRouter
@Location("/restaurant") object AllRestaurantsRoute

