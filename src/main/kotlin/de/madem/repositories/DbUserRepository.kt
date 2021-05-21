package de.madem.repositories

import de.madem.model.api.AccountRegistration
import de.madem.model.database.DBBreadUserTable
import de.madem.util.security.PasswordAuthenticator
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.any
import org.ktorm.entity.sequenceOf

class DbUserRepository(private val database: Database) {

    //#region Fields
    private val passwordAuthenticator = PasswordAuthenticator()
    //#endregion

    //#region Functions
    fun add(registration: AccountRegistration): RepositoryResponse<Boolean, Throwable> {
        val alreadyExistingUserName = database.sequenceOf(DBBreadUserTable).any { it.userName eq registration.username }

        if (alreadyExistingUserName) {
            return RepositoryResponse.Error("User is already existing")
        }

        database.insert(DBBreadUserTable) {
            set(it.userName, registration.username)
            set(it.mail, registration.mail)
            set(it.userPassword, passwordAuthenticator.hash(registration.password))
            set(it.pictureURL, null)
        }

        return RepositoryResponse.Data(true)
    }
    //#endregion

}