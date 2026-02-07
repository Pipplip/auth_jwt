package de.phbe.authjwt.user.service

import de.phbe.authjwt.user.domain.repository.UserRepository

class UserService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
)