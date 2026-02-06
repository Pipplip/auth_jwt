package de.phbe.authjwt.user.application.usecase

import de.phbe.authjwt.user.application.port.`in`.AuthenticateUserUseCase
import de.phbe.authjwt.user.application.port.out.SaveUserPort
import de.phbe.authjwt.user.adapter.security.BCryptPasswordHasher
import de.phbe.authjwt.user.adapter.security.JwtTokenProvider
import org.springframework.stereotype.Service

@Service
class AuthenticateUserService(
    private val saveUserPort: SaveUserPort,
    private val passwordHasher: BCryptPasswordHasher,
    private val jwtTokenProvider: JwtTokenProvider
) : AuthenticateUserUseCase {

    override fun authenticate(email: String, password: String): String {
        val user = saveUserPort.findByEmail(email)
            ?: throw RuntimeException("Invalid credentials")

        if (!passwordHasher.matches(password, user.passwordHash)) {
            throw RuntimeException("Invalid credentials")
        }

        return jwtTokenProvider.createToken(user.id.value, user.email)
    }
}
