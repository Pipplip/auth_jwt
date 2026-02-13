package de.phbe.authjwt.security

import de.phbe.authjwt.config.JwtProperties
import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.domain.model.UserRole
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@ActiveProfiles("test")
class JwtTokenProviderTest : FunSpec({
    val jwtProperties = JwtProperties().apply {
        secret = "mysecretkeymysecretkeymysecretkeymysecretkey"
        expirationAccess = 60000L
        expirationRefresh = 120000L
    }
    val provider = JwtTokenProvider(jwtProperties)
    val user = User(
        id = UserId(UUID.randomUUID()),
        email = "test@example.com",
        passwordHash = "irrelevant",
        registeredAt = java.time.Instant.now(),
        userRole = UserRole.USER
    )

    test("AccessToken wird korrekt erstellt und validiert") {
        val token = provider.createAccessToken(user)
        provider.validateToken(token) shouldBe true
        provider.getUserId(token) shouldBe user.id.value.toString()
    }

    test("Ungültiges Token wird abgelehnt") {
        provider.validateToken("invalid.token.value") shouldBe false
    }
})
