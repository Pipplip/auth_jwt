package de.phbe.authjwt.user.application.port.out

import de.phbe.authjwt.user.domain.model.User

interface SaveUserPort {
    fun save(user: User): User
    fun findByEmail(email: String): User?
}