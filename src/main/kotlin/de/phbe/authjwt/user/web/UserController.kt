package de.phbe.authjwt.user.web

import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.service.UserService
import de.phbe.authjwt.user.web.dto.UserResponse
import de.phbe.authjwt.user.adapter.persistence.UserMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    // DELETE: http://localhost:8080/users/ff16ce76-c8ea-4808-b146-e94cadeccfb2
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID) {
        val user = userService.findById(UserId(id))
        userService.deleteUser(user)
    }

    // GET: http://localhost:8080/users/profile
    @GetMapping("/profile")
    fun getProfile(): UserResponse {
        val currentUserId = getCurrentUserIdFromSecurityContext()
        val user = userService.findById(currentUserId)
        return UserMapper.toUserResponse(user)
    }

    private fun getCurrentUserIdFromSecurityContext(): UserId {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("No authentication found in SecurityContext")

        val principal = authentication.principal
            ?: throw IllegalStateException("No principal found in Authentication")

        // Beispiel: principal ist eine String UUID
        val id = UUID.fromString(principal.toString())
        return UserId(id)
    }

}
