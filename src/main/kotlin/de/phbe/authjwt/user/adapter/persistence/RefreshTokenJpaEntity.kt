package de.phbe.authjwt.user.adapter.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenJpaEntity(
    @Id
    val token: String,

    @Column(columnDefinition = "BINARY(16)")
    val userId: UUID,

    val expiresAt: Instant,

    val invalidated: Boolean = false,

    val invalidatedAt: Instant? = null
)