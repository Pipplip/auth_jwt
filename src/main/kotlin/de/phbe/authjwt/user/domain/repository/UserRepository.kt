package de.phbe.authjwt.user.domain.repository

import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId

interface UserRepository {
    fun findByEmail(email: String): User?
    fun save(user: User): User
}