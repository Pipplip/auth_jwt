package de.phbe.authjwt.user.adapter.persistence

import de.phbe.authjwt.user.domain.model.RefreshToken
import de.phbe.authjwt.user.domain.model.UserId
import org.springframework.stereotype.Component

@Component
class RefreshTokenMapper {
    fun toEntity(domain: RefreshToken): RefreshTokenJpaEntity =
        RefreshTokenJpaEntity(
            token = domain.token,
            userId = domain.userId.value,
            expiresAt = domain.expiresAt
        )

    fun toDomain(entity: RefreshTokenJpaEntity): RefreshToken =
        RefreshToken(
            token = entity.token,
            userId = UserId(entity.userId),
            expiresAt = entity.expiresAt
        )
}