package de.phbe.authjwt.user.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaRefreshTokenRepository :
    JpaRepository<RefreshTokenJpaEntity, String>{

    fun deleteAllByUserId(userId: UUID)
}