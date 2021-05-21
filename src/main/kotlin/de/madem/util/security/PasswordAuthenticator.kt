package de.madem.util.security

import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.spec.InvalidKeySpecException
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.experimental.xor


// https://stackoverflow.com/questions/2860943/how-can-i-hash-a-password-in-java
class PasswordAuthenticator(costVal: Int) {

    //#region Compoanion
    companion object {
        /**
         * Each token produced by this class uses this identifier as a prefix.
         */
        private const val ID = "$31$"
        /**
         * The minimum recommended cost, used by default
         */
        private const val DEFAULT_COST = 16
        private const val ALGORITHM = "PBKDF2WithHmacSHA1"
        private const val SIZE = 128
        private val layout: Pattern = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})")
    }
    //#endregion

    //#region Fields
    private var random: SecureRandom = SecureRandom()
    private val cost: Int
    //#endregion

    //#region Init
    init{
        iterations(costVal) /* Validate cost */
        this.cost = costVal
    }

    constructor() : this(DEFAULT_COST)
    //#endregion

    private fun iterations(cost: Int): Int {
        require(!(cost < 0 || cost > 30)) { "cost: $cost" }
        return 1 shl cost
    }

    /**
     * Hash a password for storage.
     *
     * @return a secure authentication token to be stored for later authentication
     */
    fun hash(password: CharArray): String {
        val salt = ByteArray(SIZE / 8)
        random.nextBytes(salt)
        val dk = pbkdf2(password, salt, 1 shl cost)
        val hash = ByteArray(salt.size + dk.size)
        System.arraycopy(salt, 0, hash, 0, salt.size)
        System.arraycopy(dk, 0, hash, salt.size, dk.size)
        val enc = Base64.getUrlEncoder().withoutPadding()
        return ID + cost + '$' + enc.encodeToString(hash)
    }

    fun hash(seq: CharSequence): String {
        return hash(seq.toString().toCharArray())
    }

    /**
     * Authenticate with a password and a stored password token.
     *
     * @return true if the password and token match
     */
    fun authenticate(password: CharArray, token: String): Boolean {
        val m: Matcher = layout.matcher(token)
        require(m.matches()) { "Invalid token format" }
        val iterations = iterations(m.group(1).toInt())
        val hash = Base64.getUrlDecoder().decode(m.group(2))
        val salt = Arrays.copyOfRange(hash, 0, SIZE / 8)
        val check = pbkdf2(password, salt, iterations)
        var zero = 0
        for (idx in check.indices) zero = zero or ((hash[salt.size + idx] xor check[idx]).toInt())
        return zero == 0
    }

    fun authenticate(seq: CharSequence, token: String): Boolean {
        return authenticate(seq.toString().toCharArray(), token)
    }


    private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int): ByteArray {
        val spec: KeySpec = PBEKeySpec(password, salt, iterations, SIZE)
        return try {
            val f = SecretKeyFactory.getInstance(ALGORITHM)
            f.generateSecret(spec).encoded
        } catch (ex: NoSuchAlgorithmException) {
            throw IllegalStateException("Missing algorithm: $ALGORITHM", ex)
        } catch (ex: InvalidKeySpecException) {
            throw IllegalStateException("Invalid SecretKeyFactory", ex)
        }
    }

}