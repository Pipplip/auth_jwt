package de.phbe.authjwt.user.domain.model

import java.time.Instant

data class User(
    val id: UserId,
    val email: String,
    val passwordHash: String,
    val userRole: UserRole,
    val registeredAt: Instant
)