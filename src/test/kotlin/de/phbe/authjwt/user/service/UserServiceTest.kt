package de.phbe.authjwt.user.service

import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.domain.model.UserRole
import de.phbe.authjwt.user.domain.repository.RefreshTokenRepository
import de.phbe.authjwt.user.domain.repository.UserRepository
import de.phbe.authjwt.user.security.PasswordHasher
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.util.UUID

@ActiveProfiles("test")
class UserServiceTest : FunSpec({
    val userRepository = mockk<UserRepository>(relaxed = true)
    val passwordHasher = mockk<PasswordHasher>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
    val service = UserService(userRepository, refreshTokenRepository, passwordHasher)
    val userId = UserId(UUID.randomUUID())
    val user = User(userId, "test@example.com", "hashed", UserRole.USER, Instant.now())

    test("User wird registriert, wenn E-Mail noch nicht existiert") {
        every { userRepository.findByEmail(any()) } returns null
        every { passwordHasher.hash(any()) } returns "hashed"
        every { userRepository.save(any()) } returns user
        val result = service.register("test@example.com", "pw")
        result.email shouldBe "test@example.com"
        verify { userRepository.save(any()) }
    }

    test("Registrierung wirft Exception bei doppelter E-Mail") {
        every { userRepository.findByEmail(any()) } returns user
        shouldThrowAny { service.register("test@example.com", "pw") }
    }

    test("Authentifizierung erfolgreich bei korrektem Passwort") {
        every { userRepository.findByEmail(any()) } returns user
        every { passwordHasher.matches(any(), any()) } returns true
        val result = service.authenticate("test@example.com", "pw")
        result shouldBe user
    }

    test("Authentifizierung schlägt fehl bei falschem Passwort") {
        every { userRepository.findByEmail(any()) } returns user
        every { passwordHasher.matches(any(), any()) } returns false
        shouldThrowAny { service.authenticate("test@example.com", "wrong") }
    }
})
