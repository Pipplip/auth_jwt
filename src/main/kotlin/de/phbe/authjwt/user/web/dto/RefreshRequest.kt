package de.phbe.authjwt.user.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RefreshRequest(
    @field:NotBlank(message = "Refresh-Token can't be empty")
    @field:Size(max = 512, message = "Invalid token format")
    val refreshToken: String
)