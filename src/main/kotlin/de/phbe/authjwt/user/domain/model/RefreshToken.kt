package de.phbe.authjwt.user.domain.model

import java.time.Instant

data class RefreshToken(
    val token: String,
    val userId: UserId,
    val expiresAt: Instant
)