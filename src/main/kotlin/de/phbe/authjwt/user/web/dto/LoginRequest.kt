package de.phbe.authjwt.user.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email is invalid")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    val password: String
)