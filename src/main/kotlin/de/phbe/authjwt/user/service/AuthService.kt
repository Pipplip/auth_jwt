package de.phbe.authjwt.user.service

import de.phbe.authjwt.config.JwtProperties
import de.phbe.authjwt.security.JwtTokenProvider
import de.phbe.authjwt.user.domain.exception.InvalidRefreshTokenException
import de.phbe.authjwt.user.domain.exception.RefreshTokenExpiredException
import de.phbe.authjwt.user.domain.model.RefreshToken
import de.phbe.authjwt.user.domain.repository.RefreshTokenRepository
import de.phbe.authjwt.user.security.RefreshTokenHasher
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
    private val jwtProperties: JwtProperties,
    private val refreshTokenHasher: RefreshTokenHasher
) {

    @Transactional
    fun login(email: String, rawPassword: String): AuthTokens {
        val user = userService.authenticate(email, rawPassword)

        val accessToken = jwtTokenProvider.createAccessToken(user)
        val refreshToken = UUID.randomUUID().toString()
        val hashedRefreshToken = refreshTokenHasher.hash(refreshToken)

        // Refresh Token auf invalid setzen oder löschen
//        refreshTokenRepository.deleteAllByUserId(user.id.value)
        refreshTokenRepository.invalidateAllByUserId(user.id.value)

        refreshTokenRepository.save(
            RefreshToken(
                token = hashedRefreshToken,
                userId = user.id,
                expiresAt = Instant.now().plusMillis(jwtProperties.expirationRefresh)
            )
        )

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    @Transactional
    fun register(email: String, rawPassword: String): AuthTokens {
        val user = userService.register(email, rawPassword)

        val accessToken = jwtTokenProvider.createAccessToken(user)
        val refreshToken = UUID.randomUUID().toString()
        val hashedRefreshToken = refreshTokenHasher.hash(refreshToken)

        refreshTokenRepository.save(
            RefreshToken(
                token = hashedRefreshToken,
                userId = user.id,
                expiresAt = Instant.now().plusMillis(jwtProperties.expirationRefresh)
            )
        )

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    @Transactional
    fun refresh(refreshToken: String): AuthTokens {
        val hashedToken = refreshTokenHasher.hash(refreshToken)

        val stored = refreshTokenRepository.findByToken(hashedToken)
            ?: throw InvalidRefreshTokenException()

        // Replay Angriff verhindern
        if (stored.invalidated) {
            refreshTokenRepository.invalidateAllByUserId(stored.userId.value)
            throw InvalidRefreshTokenException()
        }

        if (stored.isExpired()) {
//            refreshTokenRepository.delete(refreshToken)
            refreshTokenRepository.invalidate(hashedToken)
            throw RefreshTokenExpiredException()
        }

        val user = userService.findById(stored.userId)

        // Optional & empfohlen: Rotation
//        refreshTokenRepository.delete(refreshToken)
        refreshTokenRepository.invalidate(hashedToken)

        val newRefreshToken = UUID.randomUUID().toString()
        val newHashedRefreshToken = refreshTokenHasher.hash(newRefreshToken)
        refreshTokenRepository.save(
            RefreshToken(
                token = newHashedRefreshToken,
                userId = user.id,
                expiresAt = Instant.now().plusMillis(jwtProperties.expirationRefresh)
            )
        )

        val newAccessToken = jwtTokenProvider.createAccessToken(user)

        return AuthTokens(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    fun invalidateRefreshToken(refreshToken: String) {
//        refreshTokenRepository.delete(refreshToken)
        val hashedToken = refreshTokenHasher.hash(refreshToken)
        refreshTokenRepository.invalidate(hashedToken)
    }
}