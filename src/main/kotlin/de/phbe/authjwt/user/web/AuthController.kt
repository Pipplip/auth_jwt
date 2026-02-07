package de.phbe.authjwt.user.web

import de.phbe.authjwt.security.JwtTokenProvider
import de.phbe.authjwt.user.web.dto.LoginRequest
import de.phbe.authjwt.user.web.dto.JwtResponse
import org.springframework.web.bind.annotation.*
import de.phbe.authjwt.user.service.UserService
import de.phbe.authjwt.user.web.dto.RegisterRequest

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): JwtResponse  {
        // Service erstellt User
        val user = userService.register(request.email, request.password)

        // JWT erzeugen
        val token = jwtTokenProvider.createToken(user.id.value.toString(), user.email)
        return JwtResponse(token)
    }

    // Zweck: Login / Token-Ausgabe
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): JwtResponse {
        // User authentifizieren
        val user = userService.authenticate(request.email, request.password)

        // JWT erzeugen
        val token = jwtTokenProvider.createToken(user.id.value.toString(), user.email)
        return JwtResponse(token)
    }
}
