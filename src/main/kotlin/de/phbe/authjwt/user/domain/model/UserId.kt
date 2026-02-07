package de.phbe.authjwt.user.domain.model

import java.util.UUID

// @JvmInline: macht aus einem primitiven Typ einen echten Domain-Typ. Erzeugt also keine neuen Objekte.
// Macht eine Kotlin Value Class.
// Bringt später Typsicherheit: findUser(UserId(UUID.randomUUID()))

@JvmInline
value class UserId(val value: UUID)