package de.madem.model.database

import de.madem.model.*
import org.ktorm.schema.*

//#region Normal Tables
object DBDishTable : Table<DBDish>("Dish"){
    //#region Attributes
    val id = int("id").primaryKey().bindTo { it.id }
    val title = text("title").bindTo { it.title }
    val details = text("details").bindTo { it.details }
    val pictureURL = text("pictureURL").bindTo { it.pictureURL }
    val price = double("price").bindTo { it.price }
    //#endregion

    //#region Foreign Attributes
    val restaurantID = int("restaurantID").references(DBRestaurantTable) {it.restaurant}
    //#endregion
}

object DBRestaurantTable : Table<DBRestaurant>("Restaurant"){
    //#region Attributes
    val id = int("id").primaryKey().bindTo { it.id }
    val title = text("title").bindTo { it.title }
    val details = text("details").bindTo { it.details }
    val pictureURL = text("pictureURL").bindTo { it.pictureURL }
    val phone = text("phone").bindTo { it.phone }
    //#endregion

    //#region Foreign Attributes
    val addressID = int("addressID").references(DBAddressTable){ it.address }
    //#endregion
}

object DBAddressTable : Table<DBAddress>("Address"){
    //#region Attributes
    val id = int("id").primaryKey().bindTo { it.id }
    val street = text("street").bindTo { it.street }
    val houseNumber = text("houseNumber").bindTo { it.houseNumber }
    val zipCode = text("zipCode").bindTo { it.zipCode }
    val city = text("city").bindTo { it.city }
    val country = text("country").bindTo { it.country }
    val longitude = double("longitude").bindTo { it.longitude }
    val latitude = double("latitude").bindTo { it.latitude }
    //#endregion
}

object DBFoodTagTable : Table<DBFoodTag>("FoodTag"){
    //#region Attributes
    val id = int("id").primaryKey().bindTo { it.id }
    val title = text("title").bindTo { it.title }
    //#endregion
}

object DBAdditiveTable : Table<DBAdditive>("Additive"){
    //#region Attributes
    val id = int("id").primaryKey().bindTo { it.id }
    val title = text("title").bindTo { it.title }
    //#endregion
}

object DBRatingTable : Table<DBRating>("Rating") {
    //#region Attributes
    val id = int("id").primaryKey().bindTo { it.id }
    val postTime = timestamp("postTime").bindTo { it.postTime }
    val details = text("details").bindTo { it.details }
    val ratingValue = int("ratingValue").bindTo { it.ratingValue }
    //#endregion

    //#region Foreign Attributes
    val userID = int("userID").references(DBBreadUserTable) {it.user}
    val dishID = int("dishID").references(DBDishTable){it.dish}
    //#endregion
}

object DBBreadUserTable : Table<DBBreadUser>("BreadUser") {
    //#region Attributes
    val id = int("uid").primaryKey().bindTo { it.id }
    val userName = text("userName").bindTo { it.userName }
    val pictureURL = text("pictureURL").bindTo { it.pictureURL }
    val mail = text("mail").bindTo { it.mail }
    val userPassword = text("userPassword").bindTo { it.password }
    //#endregion
}
//#endregion

//#region Relationship Tables
object DBUserLikesDishTable : Table<DBUserLikesDish>("userLikesDish") {
    //#region Attributes
    val userID = int("userID").primaryKey().references(DBBreadUserTable) {it.user}
    val dishID = int("dishID").primaryKey().references(DBDishTable) {it.dish}
    //#endregion
}

object DBUserAllergicToAdditiveTable : Table<DBUserAllergicToAdditive>("userAllergicToAdditive") {
    //#region Attributes
    val userID = int("userID").primaryKey().references(DBBreadUserTable) { it.user }
    val additiveID = int("additiveID").primaryKey().references(DBAdditiveTable) {it.additive }
    //#endregion
}

object DBUserLikesFoodTagTable : Table<DBUserLikesFoodTag>("userLikesFoodTag") {

    //#region Attributes
    val userID = int("userID").primaryKey().references(DBBreadUserTable) { it.user }
    val foodTagID = int("foodTagID").primaryKey().references(DBFoodTagTable){it.foodTag}
    //#endregion
}

object DishContainsAdditivesTable : Table<DBDishContainsAdditives>("dishContainsAdditive") {
    //#region Attributes
    val dishID = int("dishID").primaryKey().references(DBDishTable) {it.dish}
    val additiveID = int("additiveID").primaryKey().references(DBAdditiveTable) {it.additive }
    //#endregion
}

object DishHasFoodTagTable : Table<DBDishHasFoodTag>("dishHasFoodTag") {
    //#region Attributes
    val dishID = int("dishID").primaryKey().references(DBDishTable) {it.dish}
    val foodTagID = int("foodTagID").primaryKey().references(DBFoodTagTable){it.foodTag}
    //#endregion
}
//#endregion