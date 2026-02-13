package de.phbe.authjwt.user.service

import de.phbe.authjwt.config.JwtProperties
import de.phbe.authjwt.security.JwtTokenProvider
import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.domain.model.UserRole
import de.phbe.authjwt.user.domain.repository.RefreshTokenRepository
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.util.UUID

@ActiveProfiles("test")
class AuthServiceTest : FunSpec({
    val userService = mockk<UserService>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
    val jwtProperties = JwtProperties().apply {
        secret = "mysecretkeymysecretkeymysecretkeymysecretkey"
        expirationAccess = 60000L
        expirationRefresh = 120000L
    }
    val jwtTokenProvider = JwtTokenProvider(jwtProperties)
    val service = AuthService(userService, refreshTokenRepository, jwtTokenProvider, jwtProperties)
    val user = User(UserId(UUID.randomUUID()), "test@example.com", "irrelevant", UserRole.USER, Instant.now())

    test("Login gibt Tokens zurück bei korrekten Credentials") {
        every { userService.authenticate(any(), any()) } returns user
        every { refreshTokenRepository.deleteAllByUserId(any()) } returns Unit
        every { refreshTokenRepository.save(any()) } returns Unit
        val tokens = service.login("test@example.com", "pw")
        tokens.accessToken.isNotBlank() shouldBe true
        tokens.refreshToken.isNotBlank() shouldBe true
    }

    test("Refresh mit ungültigem Token wirft Exception") {
        every { refreshTokenRepository.findByToken(any()) } returns null
        shouldThrowAny { service.refresh("invalid") }
    }
})
