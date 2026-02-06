package de.phbe.authjwt.user.adapter.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class UserJpaEntity(
    @Id
    val id: String,
    val email: String,
    val passwordHash: String
)
