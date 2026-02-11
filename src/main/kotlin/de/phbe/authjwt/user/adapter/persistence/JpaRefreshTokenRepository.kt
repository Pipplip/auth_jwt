package de.phbe.authjwt.user.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface JpaRefreshTokenRepository :
    JpaRepository<RefreshTokenJpaEntity, String>{

    fun deleteAllByUserId(userId: String)
}