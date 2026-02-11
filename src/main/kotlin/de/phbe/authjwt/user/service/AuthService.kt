package de.phbe.authjwt.user.service

import de.phbe.authjwt.config.JwtProperties
import de.phbe.authjwt.security.JwtTokenProvider
import de.phbe.authjwt.user.domain.model.RefreshToken
import de.phbe.authjwt.user.domain.repository.RefreshTokenRepository
import de.phbe.authjwt.user.web.dto.AuthTokens
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val userService: UserService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties
) {

    @Transactional
    fun login(email: String, rawPassword: String): AuthTokens {
        val user = userService.authenticate(email, rawPassword)

        val accessToken = jwtTokenProvider.createAccessToken(user)
        val refreshToken = UUID.randomUUID().toString()

        refreshTokenRepository.deleteAllByUserId(user.id.value)

        refreshTokenRepository.save(
            RefreshToken(
                token = refreshToken,
                userId = user.id,
                expiresAt = Instant.now().plusSeconds(jwtProperties.expirationRefresh)
            )
        )

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun register(email: String, rawPassword: String): AuthTokens {
        val user = userService.register(email, rawPassword)

        val accessToken = jwtTokenProvider.createAccessToken(user)
        val refreshToken = UUID.randomUUID().toString()

        refreshTokenRepository.save(
            RefreshToken(
                token = refreshToken,
                userId = user.id,
                expiresAt = Instant.now().plusSeconds(jwtProperties.expirationRefresh)
            )
        )

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun refresh(refreshToken: String): AuthTokens {
        val stored = refreshTokenRepository.findByToken(refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")

        if (stored.expiresAt.isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken)
            throw IllegalArgumentException("Refresh token expired")
        }

        val user = userService.findById(stored.userId)

        // Optional & empfohlen: Rotation
        refreshTokenRepository.delete(refreshToken)

        val newRefreshToken = UUID.randomUUID().toString()
        refreshTokenRepository.save(
            RefreshToken(
                token = newRefreshToken,
                userId = user.id,
                expiresAt = Instant.now().plusSeconds(jwtProperties.expirationRefresh)
            )
        )

        val newAccessToken = jwtTokenProvider.createAccessToken(user)

        return AuthTokens(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    fun invalidateRefreshToken(refreshToken: String) {
        refreshTokenRepository.delete(refreshToken)
    }
}