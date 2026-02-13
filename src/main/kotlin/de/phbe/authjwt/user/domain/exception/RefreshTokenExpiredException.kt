package de.phbe.authjwt.user.domain.exception

class RefreshTokenExpiredException(message: String = "refresh token expired") : RuntimeException(message)