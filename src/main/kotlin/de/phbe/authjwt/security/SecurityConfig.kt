package de.phbe.authjwt.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val env: Environment
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val isDev = env.acceptsProfiles(Profiles.of("dev"))

        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { header ->
                if(isDev) {
                    header.frameOptions { it.disable() }
                }
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                        //"/auth/**"
                        "/auth/register",
                        "/auth/login",
                        "/auth/refresh"
                    ).permitAll()

                    if(isDev) {
                        auth.requestMatchers(
                            "/h2-console/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/actuator/**",
                        ).permitAll()
                    }

                auth.anyRequest().authenticated()             // alles andere geschützt

            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
