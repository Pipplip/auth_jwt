package de.phbe.authjwt.user.domain.model

import java.time.Instant

data class RefreshToken(
    val token: String,
    val userId: UserId,
    val expiresAt: Instant,
    val invalidated: Boolean = false,
    val invalidatedAt: Instant? = null
) {
    fun isExpired(now: Instant = Instant.now()): Boolean =
        expiresAt.isBefore(now)

    fun isActive(now: Instant = Instant.now()): Boolean =
        !invalidated && !isExpired(now)

    fun invalidate(now: Instant = Instant.now()): RefreshToken =
        copy(
            invalidated = true,
            invalidatedAt = now
        )
}