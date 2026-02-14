package de.phbe.authjwt.user.domain.repository

import de.phbe.authjwt.user.domain.model.RefreshToken
import java.util.UUID

interface RefreshTokenRepository {
    fun save(token: RefreshToken)
    fun findByToken(token: String): RefreshToken?
    fun delete(token: String)
    fun deleteAllByUserId(userId: UUID)
    fun invalidate(token: String)
    fun invalidateAllByUserId(userId: UUID)
}