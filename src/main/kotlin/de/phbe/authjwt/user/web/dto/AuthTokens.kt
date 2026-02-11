package de.phbe.authjwt.user.web.dto

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)