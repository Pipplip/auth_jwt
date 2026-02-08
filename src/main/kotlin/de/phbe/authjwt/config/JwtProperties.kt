package de.phbe.authjwt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

// liest aus den application.properties die Properties und macht es der Anwendung zugänglich

@Component
@ConfigurationProperties(prefix = "security.jwt")
class JwtProperties {
    lateinit var secret: String
    var expirationMs: Long = 0
}