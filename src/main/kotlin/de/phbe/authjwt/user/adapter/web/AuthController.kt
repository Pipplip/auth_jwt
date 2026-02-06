package de.phbe.authjwt.user.adapter.web

import de.phbe.authjwt.user.application.port.`in`.AuthenticateUserUseCase
import de.phbe.authjwt.user.adapter.web.dto.LoginRequest
import de.phbe.authjwt.user.adapter.web.dto.JwtResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authenticateUserUseCase: AuthenticateUserUseCase) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): JwtResponse {
        val token = authenticateUserUseCase.authenticate(request.email, request.password)
        return JwtResponse(token)
    }
}
