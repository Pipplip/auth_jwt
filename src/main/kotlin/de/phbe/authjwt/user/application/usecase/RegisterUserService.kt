package de.phbe.authjwt.user.application.usecase

import de.phbe.authjwt.user.application.port.`in`.RegisterUserUseCase
import de.phbe.authjwt.user.application.port.out.SaveUserPort
import de.phbe.authjwt.user.domain.exception.UserAlreadyExistsException
import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.adapter.security.BCryptPasswordHasher
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RegisterUserService(
    private val saveUserPort: SaveUserPort,
    private val passwordHasher: BCryptPasswordHasher
) : RegisterUserUseCase {

    override fun register(email: String, password: String): String {
        if (saveUserPort.findByEmail(email) != null) {
            throw UserAlreadyExistsException(email)
        }

        val hashed = passwordHasher.hash(password)
        val user = User(
            id = UserId(UUID.randomUUID().toString()),
            email = email,
            passwordHash = hashed
        )

        saveUserPort.save(user)
        return user.id.value
    }
}