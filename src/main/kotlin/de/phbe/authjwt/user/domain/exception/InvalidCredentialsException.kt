package de.phbe.authjwt.user.domain.exception

class InvalidCredentialsException(message: String = "Invalid email or password") : RuntimeException(message)