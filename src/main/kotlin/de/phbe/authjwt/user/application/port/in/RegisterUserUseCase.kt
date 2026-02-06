package de.phbe.authjwt.user.application.port.`in`

interface RegisterUserUseCase {
    fun register(email: String, password: String): String
}