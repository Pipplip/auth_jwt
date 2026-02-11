package de.phbe.authjwt.user.adapter.persistence

import de.phbe.authjwt.user.domain.model.RefreshToken
import de.phbe.authjwt.user.domain.repository.RefreshTokenRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SpringRefreshTokenRepository(
    private val jpaRepo: JpaRefreshTokenRepository,
    private val mapper: RefreshTokenMapper
) : RefreshTokenRepository {

    override fun save(token: RefreshToken) {
        jpaRepo.save(mapper.toEntity(token))
    }

    override fun findByToken(token: String): RefreshToken? =
        jpaRepo.findById(token).orElse(null)?.let { mapper.toDomain(it) }

    override fun delete(token: String) =
        jpaRepo.deleteById(token)

    override fun deleteAllByUserId(userId: UUID) {
        jpaRepo.deleteAllByUserId(userId)
    }
}