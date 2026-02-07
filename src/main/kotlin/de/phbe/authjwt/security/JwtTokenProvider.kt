package de.phbe.authjwt.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.Date

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

    fun getUserId(token: String): String {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body.subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims: Claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}