package de.phbe.authjwt.user.security

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*

@Component
class SHA256RefreshTokenHasher : RefreshTokenHasher {

    override fun hash(token: String): String {
        require(token.isNotBlank()) { "Token must not be blank" }

        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(token.toByteArray())
        return Base64.getEncoder().encodeToString(hashedBytes)
    }

    override fun matches(token: String, hash: String): Boolean {
        return hash(token) == hash
    }
}