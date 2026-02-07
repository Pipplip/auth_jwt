package de.phbe.authjwt.user.domain.repository

import de.phbe.authjwt.user.domain.model.User

interface UserRepository {
    fun findByEmail(email: String): User?
    fun save(user: User): User
}