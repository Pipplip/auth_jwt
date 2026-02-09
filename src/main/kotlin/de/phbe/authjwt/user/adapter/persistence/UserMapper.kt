package de.phebe.authjwt.user.adapter.persistence

import de.phbe.authjwt.user.adapter.persistence.UserJpaEntity
import de.phbe.authjwt.user.domain.model.User
import de.phbe.authjwt.user.domain.model.UserId
import de.phbe.authjwt.user.web.dto.UserResponse

object UserMapper {
    fun toEntity(user: User) =
        UserJpaEntity(
            id = user.id.value,
            email = user.email,
            passwordHash = user.passwordHash,
            userRole = user.userRole,
            registeredAt = user.registeredAt
        )

    fun toDomain(entity: UserJpaEntity) =
        User(
            id = UserId(entity.id),
            email = entity.email,
            passwordHash = entity.passwordHash,
            userRole = entity.userRole,
            registeredAt = entity.registeredAt
        )

    fun toUserResponse(user: User): UserResponse =
        UserResponse(
            id = user.id.value.toString(),
            email = user.email,
            userRole = user.userRole,
            registeredAt = user.registeredAt
        )
}
