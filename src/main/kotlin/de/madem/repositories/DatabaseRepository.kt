package de.madem.repositories

import de.madem.model.DBDish
import de.madem.model.DBDishTable
import org.ktorm.database.Database
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf

object DatabaseRepository {
    private val database : Database

    init {

        //serverTimezone=Europe/Berlin url = "jdbc:mysql://116.203.201.113:3306",
        //            driver = "com.mysql.jdbc.Driver",
        //            user = "remoteroot",
        //            password = "bread#db"
        database = Database.connect(
            getDatabaseConnectionUrl()
        )
    }

    fun test() : List<DBDish>{
        return database.sequenceOf(DBDishTable).map { it }
    }

    //#region private functions
    private fun getDatabaseConnectionUrl() : String{
        val baseUrl = "jdbc:mysql://116.203.201.113:3306/breaddb"
        val urlParams = mapOf<String,Any>(
            "driver" to "com.mysql.jdbc.Driver",
            "serverTimezone" to "Europe/Berlin"
        )
        //TODO: Add valid user + password and ensure that ssl works

        return "$baseUrl?${urlParams.toList().joinToString("&"){ (key,value) -> "$key=$value" }}".also { println(it) }
    }
    //#endregion
}