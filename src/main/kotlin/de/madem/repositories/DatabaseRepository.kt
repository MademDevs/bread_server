package de.madem.repositories

import ch.qos.logback.core.db.dialect.MySQLDialect
import de.madem.model.DBDish
import de.madem.model.database.DBDishTable
import de.madem.util.security.PasswordAuthenticator
import de.madem.util.security.system.SystemProperties
import org.ktorm.database.Database
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.support.mysql.MySqlDialect
import java.sql.DriverManager

/* object DatabaseRepository {
    private val database : Database
    val users: DbUserRepository

    init {
        //TODO: change loglevel to warn or error to avoid prints of sensitive information
        database = Database.connect(logger = ConsoleLogger(threshold = LogLevel.DEBUG)){
            DriverManager.getConnection(getDatabaseConnectionUrl())
        }
        users = DbUserRepository(database)
    }

    fun test() : List<DBDish>{
        return database.sequenceOf(DBDishTable).map { it }
    }

    //#region private functions
    private fun getDatabaseConnectionUrl() : String{
        val baseUrl = "jdbc:mysql://${SystemProperties.dbhost}:${SystemProperties.dbport}/${SystemProperties.dbname}"
        val urlParams = mapOf<String,Any?>(
            "user" to SystemProperties.dbuser ,
            "password" to SystemProperties.dbpwd,
            "driver" to "com.mysql.jdbc.Driver",
            "useSSL" to false,
            "serverTimezone" to "Europe/Berlin",
            "allowPublicKeyRetrieval" to true
        )
        //"verifyServerCertificate" to false,
        //"useSSL" to true,
        //"requireSSL" to true,
        //TODO: Add valid user + password and ensure that ssl works

        return "$baseUrl?${urlParams.toList().joinToString("&"){ (key,value) -> "$key=$value" }}"
    }
    //#endregion
} */

class DatabaseRepository(private val systemProperties: SystemProperties){
    private val database : Database
    val users: DbUserRepository

    init {
        //TODO: change loglevel to warn or error to avoid prints of sensitive information
        database = Database.connect(
            logger = ConsoleLogger(threshold = LogLevel.DEBUG),
            dialect = MySqlDialect()
        ){
            DriverManager.getConnection(getDatabaseConnectionUrl())
        }
        users = DbUserRepository(database, PasswordAuthenticator())
    }

    fun test() : List<DBDish>{
        return database.sequenceOf(DBDishTable).map { it }
    }

    //#region private functions
    private fun getDatabaseConnectionUrl() : String{
        val baseUrl = "jdbc:mysql://${systemProperties.dbhost}:${systemProperties.dbport}/${systemProperties.dbname}"
        val urlParams = mapOf<String,Any?>(
            "user" to systemProperties.dbuser ,
            "password" to systemProperties.dbpwd,
            "driver" to "com.mysql.jdbc.Driver",
            "useSSL" to false,
            "serverTimezone" to "Europe/Berlin",
            "allowPublicKeyRetrieval" to true
        )
        //"verifyServerCertificate" to false,
        //"useSSL" to true,
        //"requireSSL" to true,
        //TODO: Add valid user + password and ensure that ssl works

        return "$baseUrl?${urlParams.toList().joinToString("&"){ (key,value) -> "$key=$value" }}"
    }
    //#endregion
}