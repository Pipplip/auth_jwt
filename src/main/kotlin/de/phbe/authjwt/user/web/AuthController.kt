package de.phbe.authjwt.user.web

import de.phbe.authjwt.user.service.AuthService
import de.phbe.authjwt.user.web.dto.AuthTokens
import de.phbe.authjwt.user.web.dto.LoginRequest
import de.phbe.authjwt.user.web.dto.RefreshRequest
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val maxAgeCookie: Long = 7 * 24 * 60 * 60L, // 7 Tage in Sekunden
    private val useCookies: Boolean = true
) {
    // Zweck: Login / Tokens-Ausgabe
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthTokens> {
        val tokens = authService.login(request.email, request.password)

        if(useCookies) {
            // Setze Refresh Token in HTTP-only Cookie
            // um clientseitige Zugriffe zu verhindern (XSS-Schutz)
            val cookie = ResponseCookie.from("refreshToken", tokens.refreshToken)
                .httpOnly(true)
                .secure(false) // HTTP und HTTPS, bei true würde nur HTTPS funktionieren
                .path("/auth/refresh")
                .maxAge(maxAgeCookie) // 7 Tage
                .sameSite("Strict")
                .build()

            return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(AuthTokens(tokens.accessToken, "")) // Refresh Token nur in Cookie
        }else{
            return ResponseEntity.ok(
                AuthTokens(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken
                )
            )
        }
    }

    // Ohne Cookies würde der Client das Refresh Token im Body senden, z.B. im Authorization Header oder als JSON Payload
//    @PostMapping("/refresh")
//    fun refresh(@RequestBody request: RefreshRequest): ResponseEntity<AuthTokens> {
//        val tokens = authService.refresh(request.refreshToken)
//
//        return ResponseEntity.ok(
//            AuthTokens(
//                accessToken = tokens.accessToken,
//                refreshToken = tokens.refreshToken
//            )
//        )
//    }

    @PostMapping("/refresh")
    fun refresh(@CookieValue("refreshToken") refreshToken: String?): ResponseEntity<AuthTokens> {
        if (refreshToken.isNullOrBlank()) {
            return ResponseEntity.status(401).build()
        }

        val tokens = authService.refresh(refreshToken)

        // Rotierte Refresh Token zurück in Cookie
        val cookie = ResponseCookie.from("refreshToken", tokens.refreshToken)
            .httpOnly(true)
            .secure(false) // HTTP und HTTPS, bei true würde nur HTTPS funktionieren
            .path("/auth/refresh")
            .maxAge(maxAgeCookie)
            .sameSite("Strict")
            .build()

        return ResponseEntity.ok()
            .header("Set-Cookie", cookie.toString())
            .body(AuthTokens(tokens.accessToken, ""))
    }

    // Ohne Cookies würde der Client das Refresh Token im Body senden, z.B. im Authorization Header oder als JSON Payload
//    @PostMapping("/logout")
//    fun logout(@RequestBody request: RefreshRequest): ResponseEntity<Void> {
//        authService.invalidateRefreshToken(request.refreshToken)
//        return ResponseEntity.noContent().build()
//    }

    @PostMapping("/logout")
    fun logout(@CookieValue("refreshToken") refreshToken: String?): ResponseEntity<Void> {
        if (!refreshToken.isNullOrBlank()) {
            authService.invalidateRefreshToken(refreshToken)
        }

        // Cookie sofort löschen
        val cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(false) // HTTP und HTTPS, bei true würde nur HTTPS funktionieren
            .path("/auth/refresh")
            .maxAge(0) // Cookie sofort löschen
            .sameSite("Strict")
            .build()

        return ResponseEntity.noContent()
            .header("Set-Cookie", cookie.toString())
            .build()
    }
}
