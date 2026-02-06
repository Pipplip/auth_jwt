package de.phbe.authjwt.user.domain.model

data class User(
    val id: UserId,
    val email: String,
    val passwordHash: String
)