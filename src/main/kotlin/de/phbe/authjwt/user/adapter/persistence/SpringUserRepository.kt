package de.phbe.authjwt.user.adapter.persistence

import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.domain.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class SpringUserRepository(
    private val jpaRepository: JpaUserRepository
) : UserRepository {

    override fun findByEmail(email: String): User? {
        return jpaRepository.findByEmail(email)?.let { UserMapper.toDomain(it) }
    }

    override fun findById(userId: UserId): User? {
        return jpaRepository.findById(userId.value).orElse(null)?.let { UserMapper.toDomain(it) }
    }

    override fun save(user: User): User {
        val entity = UserMapper.toEntity(user)
        val saved = jpaRepository.save(entity)
        return UserMapper.toDomain(saved)
    }

    override fun delete(user: User) {
        jpaRepository.deleteById(user.id.value)
    }
}