package de.phbe.authjwt.user.adapter.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider {
    private val secret = "my-secret-key" // Im realen Projekt extern speichern
    private val validityInMs: Long = 3600000 // 1 Stunde

    fun createToken(userId: String, email: String): String {
        val claims = Jwts.claims().setSubject(userId)
        claims["email"] = email

        val now = Date()
        val validity = Date(now.time + validityInMs)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }
}
