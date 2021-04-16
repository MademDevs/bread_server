package de.madem.repositories

import de.madem.model.DBDish
import de.madem.model.DBDishTable
import org.ktorm.database.Database
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf

object DatabaseRepository {
    private val database : Database

    init {

        database = Database.connect(
            getDatabaseConnectionUrl()
        )
    }

    fun test() : List<DBDish>{
        return database.sequenceOf(DBDishTable).map { it }
    }

    //#region private functions
    private fun getDatabaseConnectionUrl() : String{
        val baseUrl = "baseUrl"
        val urlParams = mapOf<String,Any>(
            "driver" to "com.mysql.jdbc.Driver",
            "serverTimezone" to "Europe/Berlin"
        )
        //TODO: Add valid user + password and ensure that ssl works

        return "$baseUrl?${urlParams.toList().joinToString("&"){ (key,value) -> "$key=$value" }}".also { println(it) }
    }
    //#endregion
}