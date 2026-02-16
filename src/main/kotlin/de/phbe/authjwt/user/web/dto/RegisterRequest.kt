package de.phbe.authjwt.user.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email is invalid")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 6, message = "Password must have at least 6 characters")
    val password: String
)