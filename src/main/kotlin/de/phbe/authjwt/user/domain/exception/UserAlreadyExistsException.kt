package de.phbe.authjwt.user.domain.exception

class UserAlreadyExistsException(email: String) : RuntimeException("User with email $email already exists")
