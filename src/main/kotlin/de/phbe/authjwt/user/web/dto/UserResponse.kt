package de.phbe.authjwt.user.web.dto

import de.phbe.authjwt.user.domain.model.UserRole
import java.time.Instant

data class UserResponse(
    val id: String,
    val email: String,
    val userRole: UserRole,
    val registeredAt: Instant
)