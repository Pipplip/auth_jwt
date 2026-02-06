package de.phbe.authjwt.user.adapter.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordHasher {
    private val encoder = BCryptPasswordEncoder()

    fun hash(password: String) = encoder.encode(password)
    fun matches(raw: String, hash: String) = encoder.matches(raw, hash)
}
