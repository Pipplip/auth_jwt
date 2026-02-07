package de.phbe.authjwt.user.service

import de.phbe.authjwt.user.domain.exception.UserAlreadyExistsException
import de.phbe.authjwt.user.domain.exception.UserNotFoundException
import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.domain.repository.UserRepository
import de.phbe.authjwt.user.security.PasswordHasher
import java.time.Instant
import java.util.UUID

// verwendet zwei ports
class UserService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
){
    fun register(email: String, rawPassword: String): User {
        // 1 Fachliche Regel: E-Mail muss eindeutig sein
        if (userRepository.findByEmail(email) != null) {
            throw UserAlreadyExistsException(email)
        }

        // 2 User erzeugen (Domain-Zustand)
        val user = User(
            id = UserId(UUID.randomUUID()),
            email = email,
            passwordHash = passwordHasher.hash(rawPassword),
            registeredAt = Instant.now()
        )

        // 3 Persistieren
        userRepository.save(user)

        // 4 Rückgabe (optional, aber praktisch)
        return user
    }

    fun findById(id: UserId): User {
        return userRepository.findById(id)
            ?: throw UserNotFoundException(id)
    }

    fun authenticate(email: String, rawPassword: String): User {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordHasher.matches(rawPassword, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        return user
    }

    fun deleteUser(user: User) {
        // hier könnten später Fachregeln stehen:
        // - darf sich selbst löschen?
        // - nur Admin?
        // - Soft Delete?

        userRepository.delete(user)
    }
}