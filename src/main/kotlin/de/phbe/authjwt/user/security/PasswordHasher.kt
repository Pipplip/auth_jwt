package de.phbe.authjwt.user.security

// outbound port
interface PasswordHasher {
    fun hash(rawPassword: String): String?
    fun matches(rawPassword: String, hashedPassword: String): Boolean
}