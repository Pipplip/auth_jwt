package de.phbe.authjwt.user.web

import de.phbe.authjwt.user.service.AuthService
import de.phbe.authjwt.user.web.dto.AuthTokens
import de.phbe.authjwt.user.web.dto.LoginRequest
import org.springframework.web.bind.annotation.*
import de.phbe.authjwt.user.web.dto.RefreshRequest
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    // Zweck: Login / Tokens-Ausgabe
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthTokens> {
        val tokens = authService.login(request.email, request.password)

        return ResponseEntity.ok(
            AuthTokens(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken
            )
        )
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): ResponseEntity<AuthTokens> {
        val tokens = authService.refresh(request.refreshToken)

        return ResponseEntity.ok(
            AuthTokens(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken
            )
        )
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshRequest): ResponseEntity<Void> {
        authService.invalidateRefreshToken(request.refreshToken)
        return ResponseEntity.noContent().build()
    }
}
