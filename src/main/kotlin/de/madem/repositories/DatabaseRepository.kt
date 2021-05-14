package de.madem.repositories

import de.madem.model.DBDish
import de.madem.model.DBDishTable
import org.ktorm.database.Database
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import java.sql.DriverManager

object DatabaseRepository {
    private val database : Database

    init {
        database = Database.connect{
            DriverManager.getConnection(getDatabaseConnectionUrl())
        }
    }

    fun test() : List<DBDish>{
        return database.sequenceOf(DBDishTable).map { it }
    }

    //#region private functions
    private fun getDatabaseConnectionUrl() : String{

        val baseUrl = "jdbc:mysql://localhost:3306/breaddb"
        val urlParams = mapOf<String,Any>(
            "user" to "username",
            "password" to "password",
            "driver" to "com.mysql.jdbc.Driver",
            "useSSL" to false,
            "serverTimezone" to "Europe/Berlin",
            "allowPublicKeyRetrieval" to true
        )
        //"verifyServerCertificate" to false,
        //"useSSL" to true,
        //"requireSSL" to true,
        //TODO: Add valid user + password and ensure that ssl works

        return "$baseUrl?${urlParams.toList().joinToString("&"){ (key,value) -> "$key=$value" }}".also { println(it) }
    }
    //#endregion
}