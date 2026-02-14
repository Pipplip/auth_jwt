package de.phbe.authjwt.user.service

import de.phbe.authjwt.user.domain.exception.InvalidCredentialsException
import de.phbe.authjwt.user.domain.exception.UnauthorizedException
import de.phbe.authjwt.user.domain.exception.UserAlreadyExistsException
import de.phbe.authjwt.user.domain.exception.UserNotFoundException
import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.domain.model.UserRole
import de.phbe.authjwt.user.domain.repository.RefreshTokenRepository
import de.phbe.authjwt.user.domain.repository.UserRepository
import de.phbe.authjwt.user.security.PasswordHasher
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

// verwendet zwei ports
@Service
class UserService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordHasher: PasswordHasher,
) {
    fun register(
        email: String,
        rawPassword: String,
    ): User {
        // 1 Fachliche Regel: E-Mail muss eindeutig sein
        if (userRepository.findByEmail(email) != null) {
            throw UserAlreadyExistsException(email)
        }

        // 2 User erzeugen (Domain-Zustand)
        val user =
            User(
                id = UserId(UUID.randomUUID()),
                email = email,
                passwordHash = passwordHasher.hash(rawPassword),
                registeredAt = Instant.now(),
                userRole = UserRole.USER
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
            ?: throw InvalidCredentialsException()

        if (!passwordHasher.matches(rawPassword, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        return user
    }

    fun deleteUser(userToDelete: User, currentUser: User) {
        // nur admins dürfen löschen
        // und man darf sich nicht selbst löschen (sonst könnte man sich aus dem System aussperren)
        // println("deleting user ${currentUser.id} - ${userToDelete.id}  - role ${userToDelete.userRole}")
        if (currentUser.id == userToDelete.id && currentUser.userRole != UserRole.ADMIN) {
            throw UnauthorizedException()
        }
        refreshTokenRepository.invalidateAllByUserId(userToDelete.id.value) // alle refreshTokens des Benutzers invalidieren
        userRepository.delete(userToDelete)
    }
}
