package de.madem.system

object Environment {
    val dbuser = System.getenv("dbuser")
    val dbpwd = System.getenv("dbpwd")
}