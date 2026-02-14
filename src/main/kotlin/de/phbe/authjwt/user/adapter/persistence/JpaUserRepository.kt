package de.phbe.authjwt.user.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

// Spring Data Interface
interface JpaUserRepository : JpaRepository<UserJpaEntity, UUID> {
    // SELECT * FROM users WHERE email = ?
    // Keine Standard-Crud Funktion von JpaRepository
    fun findByEmail(email: String): UserJpaEntity?
}