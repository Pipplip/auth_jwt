package de.phbe.authjwt.user.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

interface JpaRefreshTokenRepository :
    JpaRepository<RefreshTokenJpaEntity, String>{

    fun deleteAllByUserId(userId: UUID)

    @Modifying
    @Transactional
    @Query("""
        update RefreshTokenJpaEntity t
        set t.invalidated = true,
            t.invalidatedAt = :now
        where t.token = :token
    """)
    fun invalidateToken(token: String, now: Instant)

    @Modifying
    @Transactional
    @Query("""
        update RefreshTokenJpaEntity t
        set t.invalidated = true,
            t.invalidatedAt = :now
        where t.userId = :userId
    """)
    fun invalidateAllByUserId(userId: UUID, now: Instant)
}