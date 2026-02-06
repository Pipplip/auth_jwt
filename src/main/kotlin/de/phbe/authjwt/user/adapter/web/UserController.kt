package de.phbe.authjwt.user.adapter.web

import de.phbe.authjwt.user.application.port.`in`.RegisterUserUseCase
import de.phbe.authjwt.user.adapter.web.dto.RegisterRequest
import de.phbe.authjwt.user.adapter.web.dto.UserResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val registerUserUseCase: RegisterUserUseCase) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): UserResponse {
        val id = registerUserUseCase.register(request.email, request.password)
        return UserResponse(id, request.email)
    }

//    @GetMapping("/profile")
//    fun profile(authentication: UsernamePasswordAuthenticationToken): UserResponse {
//        val userId = authentication.principal as String
//        // User aus Repository holen
//        return UserResponse(userId, "user@example.com")
//    }
}
