package de.phbe.authjwt.user.domain.repository

import de.phbe.authjwt.user.domain.model.RefreshToken

interface RefreshTokenRepository {
    fun save(token: RefreshToken)
    fun findByToken(token: String): RefreshToken?
    fun delete(token: String)
    fun deleteAllByUserId(userId: String)
}