package com.ezbookkeeping.android.service

import com.ezbookkeeping.android.data.db.entity.UserEntity
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TotpService @Inject constructor() {
    private val random = SecureRandom()

    fun generateSecret(): String {
        val bytes = ByteArray(20)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun generateCode(secret: String): String {
        val key = secret.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        return GoogleAuthenticator(key).generate(java.util.Date(System.currentTimeMillis()))
    }

    fun verifyCode(secret: String, code: String): Boolean {
        val key = secret.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val authenticator = GoogleAuthenticator(key)
        val currentWindow = System.currentTimeMillis() / 30000
        return (currentWindow - 1..currentWindow + 1).any { window ->
            authenticator.generate(java.util.Date(window * 30000)) == code
        }
    }
}
