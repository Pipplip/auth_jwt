package de.phbe.authjwt.user.adapter.persistence

import de.phbe.authjwt.user.application.port.out.SaveUserPort
import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

interface SpringUserJpaRepository : JpaRepository<UserJpaEntity, String> {
    fun findByEmail(email: String): UserJpaEntity?
}

@Component
class SpringUserRepository(
    private val repository: SpringUserJpaRepository
) : SaveUserPort {

    override fun save(user: User): User {
        val entity = UserJpaEntity(user.id.value, user.email, user.passwordHash)
        repository.save(entity)
        return user
    }

    override fun findByEmail(email: String): User? {
        val entity = repository.findByEmail(email) ?: return null
        return User(UserId(entity.id), entity.email, entity.passwordHash)
    }
}
