package de.madem.model

import org.ktorm.entity.Entity
import java.time.Instant

//#region Normal Entities
interface DBDish : Entity<DBDish>{
    companion object : Entity.Factory<DBDish>()

    //#region Attributes
    val id : Int
    val title : String
    val details : String
    val pictureURL : String?
    val price : Double
    //#endregion

    //#region Foreign Attributes
    val restaurant : DBRestaurant
    //#endregion
}

interface DBRestaurant : Entity<DBRestaurant>{
    companion object : Entity.Factory<DBRestaurant>()

    //#region Attributes
    val id : Int
    val title : String
    val details : String
    val pictureURL : String?
    val phone : String?
    //#endregion

    //#region Foreign Attributes
    val address : DBAddress
    //#endregion
}

interface DBAddress : Entity<DBAddress>{
    companion object : Entity.Factory<DBAddress>()

    //#region Attributes
    val id : Int
    val street : String
    val houseNumber : String
    val zipCode : String
    val city : String
    val country : String
    val long : Double
    val lat : Double
    //#endregion
}

interface DBFoodTag : Entity<DBFoodTag>{
    companion object : Entity.Factory<DBFoodTag>()

    //#region Attributes
    val id : Int
    val title : String
    //#endregion
}

interface DBAdditive : Entity<DBAdditive>{
    companion object : Entity.Factory<DBAdditive>()

    //#region Attributes
    val id : Int
    val title : String
    //#endregion
}

interface DBRating : Entity<DBRating>{
    companion object : Entity.Factory<DBRating>()

    //#region Attributes
    val id : Int
    val postTime : Instant
    val details : String?
    val ratingValue : Int
    //#endregion

    //#region Foreign Attributes
    val user : DBBreadUser
    val dish : DBDish
    //#endregion
}

interface DBBreadUser : Entity<DBBreadUser>{
    companion object : Entity.Factory<DBBreadUser>()

    //#region Attributes
    val id : Int
    val userName : String
    val pictureURL : String?
    val mail : String
    val password : String
    //#endregion
}
//#endregion

//#region Relationship Entities
interface DBUserLikesDish : Entity<DBUserLikesDish>{
    companion object : Entity.Factory<DBUserLikesDish>()

    //#region Attributes
    val user : DBBreadUser
    val dish : DBDish
    //#endregion
}

interface DBUserAllergicToAdditive : Entity<DBUserAllergicToAdditive>{
    companion object : Entity.Factory<DBUserAllergicToAdditive>()

    //#region Attributes
    val user : DBBreadUser
    val additive : DBAdditive
    //#endregion
}

interface DBUserLikesFoodTag : Entity<DBUserLikesFoodTag>{
    companion object : Entity.Factory<DBUserLikesFoodTag>()

    //#region Attributes
    val user : DBBreadUser
    val foodTag : DBFoodTag
    //#endregion
}

interface DBDishContainsAdditives : Entity<DBDishContainsAdditives>{
    companion object : Entity.Factory<DBDishContainsAdditives>()

    //#region Attributes
    val dish : DBDish
    val additive : DBAdditive
    //#endregion
}

interface DBDishHasFoodTag : Entity<DBDishHasFoodTag>{
    companion object : Entity.Factory<DBDishHasFoodTag>()

    //#region Attributes
    val dish : DBDish
    val foodTag : DBFoodTag
    //#endregion
}
//#endregion