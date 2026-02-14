package de.phbe.authjwt.user.security

interface RefreshTokenHasher {
    fun hash(token: String): String
    fun matches(token: String, hash: String): Boolean
}