package de.phbe.authjwt.user.adapter.security

import de.phbe.authjwt.user.security.PasswordHasher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordHasher : PasswordHasher {
    private val encoder = BCryptPasswordEncoder()

    override fun hash(rawPassword: String) = encoder.encode(rawPassword)
    override fun matches(rawPassword: String, hashedPassword: String) =
        encoder.matches(rawPassword, hashedPassword)
}
