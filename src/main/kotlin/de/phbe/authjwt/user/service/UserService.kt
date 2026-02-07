package de.phbe.authjwt.user.service

import de.phbe.authjwt.user.domain.repository.UserRepository
import de.phbe.authjwt.user.security.PasswordHasher

// verwendet zwei ports
class UserService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
)