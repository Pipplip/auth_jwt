package de.phbe.authjwt.user.domain.repository

import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId

// Outbound port
// sagt nur was gebraucht wird, weiss nichts von JPA, Spring etc.
// beschreibt fachlichen Zugriff auf User
interface UserRepository {
    fun findByEmail(email: String): User?
    fun findById(id: UserId): User?
    fun save(user: User): User
    fun delete(user: User)
}