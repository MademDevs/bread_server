package de.madem

import de.madem.util.security.PasswordAuthenticator
import org.junit.Test

import kotlin.test.assertTrue


class PasswordHashTest {

    private val pwdAuth = PasswordAuthenticator()

    @Test
    fun testPwdHashing() {
        val pwd1 = "Secret#PWD123"
        val pwd2 = "Secret#PWD123"
        val pwd3 = "otherPWD#123"

        val hashed1 = pwdAuth.hash(pwd1)
        val hashed2 = pwdAuth.hash(pwd2)
        val hashed3 = pwdAuth.hash(pwd3)

        assertTrue { pwdAuth.authenticate(pwd1,hashed1) }
        assertTrue { pwdAuth.authenticate(pwd2,hashed2) }
        assertTrue { pwdAuth.authenticate(pwd3,hashed3) }

    }
}