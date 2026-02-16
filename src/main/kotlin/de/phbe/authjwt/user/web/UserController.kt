package de.phbe.authjwt.user.web

import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.service.UserService
import de.phbe.authjwt.user.web.dto.UserResponse
import de.phbe.authjwt.user.adapter.persistence.UserMapper
import de.phbe.authjwt.user.domain.exception.UnauthorizedException
import de.phbe.authjwt.user.domain.model.User
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    // DELETE: http://localhost:8080/users/ff16ce76-c8ea-4808-b146-e94cadeccfb2
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID) {
        val currentUser = getCurrentUserFromSecurityContext()
        val userToDelete = userService.findById(UserId(id))
        userService.deleteUser(userToDelete, currentUser)
    }

    // GET: http://localhost:8080/users/profile
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    fun getProfile(): UserResponse {
        val user = getCurrentUserFromSecurityContext()
        return UserMapper.toUserResponse(user)
    }

    private fun getCurrentUserFromSecurityContext(): User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw UnauthorizedException("No authentication found")

        val principal = authentication.principal
            ?: throw UnauthorizedException("No principal found")

        // Beispiel: principal ist eine String UUID
        val id = runCatching { UUID.fromString(principal.toString()) }
            .getOrElse { throw UnauthorizedException("Invalid principal format") }

        return userService.findById(UserId(id))
    }

}
