package de.phbe.authjwt.user.application.port.`in`

interface AuthenticateUserUseCase {
    fun authenticate(email: String, password: String): String
}