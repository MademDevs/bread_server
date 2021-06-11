package de.madem.model.api

import de.madem.util.nullhandling.NonNullableConverter

data class NullableAccountRegistration(
    val username: String?,
    val password: String?,
    val mail: String?
) : NonNullableConverter<AccountRegistration>{
    override fun notNullable(): AccountRegistration = AccountRegistration(username!!, password!!,mail!!)
}
data class AccountRegistration(val username: String, val password: String, val mail: String)

data class NullableAccountLoginRequest(val username: String?, val password: String?) : NonNullableConverter<AccountLoginRequest>{
    override fun notNullable() : AccountLoginRequest = AccountLoginRequest(username!!,password!!)
}
data class AccountLoginRequest(val username: String, val password: String)

data class AccountLoginResponse(val token: String?, val uid: Int?)


data class NullableRestaurantInfo(
    val restaurant: NullableRestaurant?,
    val address: NullableAddress?,
    val dishes: List<NullableDish>?
) : NonNullableConverter<RestaurantInfo> {
    override fun notNullable(): RestaurantInfo {
        return RestaurantInfo(
            restaurant!!.notNullable(),
            address!!.notNullable(),
            dishes?.map { it.notNullable() } ?: emptyList()
        )
    }
}
data class NullableRestaurant(
    val title: String?,
    val details: String?,
    val pictureURL : String?,
    val phone: String?
) : NonNullableConverter<Restaurant>{
    override fun notNullable(): Restaurant = Restaurant(title!!,details ?: "",pictureURL,phone!!)
}
data class NullableAddress(
    val street: String?,
    val number: String?,
    val zipCode: String?,
    val city: String?,
    val country: String?
) : NonNullableConverter<Address>{
    override fun notNullable(): Address = Address(street!!,number!!,zipCode!!,city!!,country!!)
}

data class NullableDish(
    val title: String?,
    val details: String?,
    val pictureURL : String?,
    val price: Double?,
    val additives: List<NullableAdditive>?,
    val foodTags: List<NullableFoodTag>?
) : NonNullableConverter<Dish>{
    override fun notNullable(): Dish {
        val nnAdditives = additives?.map { it.notNullable() } ?: emptyList()
        val nnFoodTags = foodTags?.map { it.notNullable() } ?: emptyList()

        return Dish(title!!,details ?: "",pictureURL,price!!,nnAdditives, nnFoodTags)
    }
}
data class NullableFoodTag(val title: String?) : NonNullableConverter<FoodTag>{
    override fun notNullable(): FoodTag = FoodTag(title!!)
}
data class NullableAdditive(val title: String?) : NonNullableConverter<Additive>{
    override fun notNullable(): Additive = Additive(title!!)
}


data class RestaurantInfo(val restaurant: Restaurant, val address: Address, val dishes: List<Dish>)
data class Restaurant(val title: String, val details: String,val pictureURL : String?, val phone: String)
data class Address(val street: String, val number: String, val zipCode: String, val city: String, val country: String)
data class Dish(
    val title: String,
    val details: String,
    val pictureURL : String?,
    val price: Double,
    val additives: List<Additive>,
    val foodTags: List<FoodTag>
)
data class FoodTag(val title: String)
data class Additive(val title: String)

