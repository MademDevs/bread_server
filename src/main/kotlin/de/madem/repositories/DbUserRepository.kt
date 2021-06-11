package de.madem.repositories

import de.madem.model.DBBreadUser
import de.madem.model.api.AccountRegistration
import de.madem.model.api.NullableAccountRegistration
import de.madem.model.database.DBBreadUserTable
import de.madem.util.security.PasswordAuthenticator
import io.ktor.features.NotFoundException
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.any
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf

class DbUserRepository(private val database: Database, private val passwordAuthenticator: PasswordAuthenticator) {

    //#region Functions
    fun add(registration: AccountRegistration): RepositoryResponse<Int, Throwable> {
        val alreadyExistingUserName = database.sequenceOf(DBBreadUserTable).any { it.userName eq registration.username }

        if (alreadyExistingUserName) {
            return RepositoryResponse.Error("User is already existing")
        }

        val genId = database.insertAndGenerateKey(DBBreadUserTable) {
            set(it.userName, registration.username)
            set(it.mail, registration.mail)
            set(it.userPassword, passwordAuthenticator.hash(registration.password))
            set(it.pictureURL, null)
        }

        return RepositoryResponse.Data(genId as Int)
    }

    fun getUserByUsername(userName : String): RepositoryResponse<DBBreadUser, Throwable> {
        val user = database
            .sequenceOf(DBBreadUserTable)
            .firstOrNull { it.userName eq userName }
            ?: return RepositoryResponse.Error(NotFoundException("User not found"))
        return RepositoryResponse.Data(user)
    }

    fun isCorrectPassword(password: String, hashedPwd: String) : Boolean
        = passwordAuthenticator.authenticate(password, hashedPwd)
    //#endregion

}