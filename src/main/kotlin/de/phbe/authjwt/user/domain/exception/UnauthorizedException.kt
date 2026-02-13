package de.phbe.authjwt.user.domain.exception

class UnauthorizedException(message: String = "Unauthorized") : RuntimeException(message)