package de.phbe.authjwt.user.web.dto

import java.time.Instant

data class UserResponse(
    val id: String,
    val email: String,
    val registeredAt: Instant
)