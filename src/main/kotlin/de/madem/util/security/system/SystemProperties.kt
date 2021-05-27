package de.madem.util.security.system

/*object SystemProperties {
    val dbuser: String? = System.getenv("dbuser")
    val dbpwd: String? = System.getenv("dbpwd")
    val dbhost: String? = System.getenv("dbhost")
    val dbname: String? = System.getenv("dbname")
    val dbport: String? = System.getenv("dbport")
    val jwtsecret : String? = System.getenv("jwtsecret")
}*/

class SystemProperties (
    val dbuser: String? = System.getenv("dbuser"),
    val dbpwd: String? = System.getenv("dbpwd"),
    val dbhost: String? = System.getenv("dbhost"),
    val dbname: String? = System.getenv("dbname"),
    val dbport: String? = System.getenv("dbport"),
    val jwtsecret : String? = System.getenv("jwtsecret")
)