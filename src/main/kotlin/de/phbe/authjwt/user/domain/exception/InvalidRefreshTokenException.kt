package de.phbe.authjwt.user.domain.exception

class InvalidRefreshTokenException(message: String = "Invalid refresh token") : RuntimeException(message)