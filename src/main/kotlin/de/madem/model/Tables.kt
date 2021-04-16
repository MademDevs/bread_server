package de.madem.model
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
    //TODO : add props
}

//TODO: add further tables
//#endregion

//#region Relationship Tables
//TODO: Add further tables
//#endregion