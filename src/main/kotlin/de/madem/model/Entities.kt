package de.madem.model

import org.ktorm.entity.Entity
import java.sql.Timestamp

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
    val postTime : Timestamp
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
interface UserLikesDish : Entity<UserLikesDish>{
    companion object : Entity.Factory<UserLikesDish>()

    //#region Attributes
    val user : DBBreadUser
    val dish : DBDish
    //#endregion
}

interface UserAllergicToAdditive : Entity<UserAllergicToAdditive>{
    companion object : Entity.Factory<UserAllergicToAdditive>()

    //#region Attributes
    val user : DBBreadUser
    val additive : DBAdditive
    //#endregion
}

interface UserLikesFoodTag : Entity<UserLikesFoodTag>{
    companion object : Entity.Factory<UserLikesFoodTag>()

    //#region Attributes
    val user : DBBreadUser
    val foodTag : DBFoodTag
    //#endregion
}

interface DishContainsAdditives : Entity<DishContainsAdditives>{
    companion object : Entity.Factory<DishContainsAdditives>()

    //#region Attributes
    val dish : DBDish
    val additive : DBAdditive
    //#endregion
}

interface DishHasFoodTag : Entity<DishHasFoodTag>{
    companion object : Entity.Factory<DishHasFoodTag>()

    //#region Attributes
    val dish : DBDish
    val foodTag : DBFoodTag
    //#endregion
}
//#endregion