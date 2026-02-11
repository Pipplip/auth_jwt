package de.phbe.authjwt.security

import de.phbe.authjwt.config.JwtProperties
import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
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