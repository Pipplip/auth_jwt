package de.phbe.authjwt.user.domain.exception

import de.phbe.authjwt.user.domain.model.UserId

class UserNotFoundException(userId: UserId) : RuntimeException("User with email $userId already exists")