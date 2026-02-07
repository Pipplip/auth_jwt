package de.phbe.authjwt.user.domain.repository

import de.phbe.authjwt.user.domain.model.User

// Outbound port
// sagt nur was gebraucht wird, weiss nichts von JPA, Spring etc.
interface UserRepository {
    fun findByEmail(email: String): User?
    fun save(user: User): User
}