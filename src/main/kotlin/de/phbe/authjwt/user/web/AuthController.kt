package de.phbe.authjwt.user.web

import de.phbe.authjwt.user.adapter.security.JwtTokenProvider
import de.phbe.authjwt.user.web.dto.LoginRequest
import de.phbe.authjwt.user.web.dto.JwtResponse
import org.springframework.web.bind.annotation.*
import de.phbe.authjwt.user.service.UserService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    // Zweck: Login / Token-Ausgabe
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): JwtResponse {
        val user = userService.authenticate(request.email, request.password)
        return JwtResponse(jwtTokenProvider.createToken(user.id))
    }
}
