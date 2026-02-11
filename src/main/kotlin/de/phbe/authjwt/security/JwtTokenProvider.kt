package de.phbe.authjwt.security

import de.phbe.authjwt.config.JwtProperties
import de.phbe.authjwt.user.domain.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val secret = jwtProperties.secret
    private val validityAccessTokenInMs = jwtProperties.expirationAccess

    fun createAccessToken(user: User): String {
        val claims = Jwts.claims().setSubject(user.id.value.toString())
        claims["email"] = user.email
        claims["role"] = user.userRole.name

        val now = Date()
        val validity = Date(now.time + validityAccessTokenInMs)

        val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key)
            .compact()
    }

    fun getUserId(token: String): String {
        val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
            val claims: Claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}