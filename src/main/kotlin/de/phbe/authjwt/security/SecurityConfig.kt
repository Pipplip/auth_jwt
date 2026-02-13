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
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .headers { header ->
                if(env.acceptsProfiles(Profiles.of("dev"))) {
                    header.frameOptions { it.disable() }
                }
            }
            .authorizeHttpRequests { auth ->
                auth
//                    .requestMatchers("/auth/**").permitAll()  // Register/Login öffentlich
                    .requestMatchers("/auth/register").permitAll()  // Register/Login öffentlich
                    .requestMatchers("/auth/login").permitAll()  // Register/Login öffentlich
                    // H2-Konsole nur freigeben, wenn Dev-Profil aktiv
                    .requestMatchers("/h2-console/**").let {
                        if (env.acceptsProfiles(Profiles.of("dev"))) it.permitAll() else it.denyAll()
                    }
                    .anyRequest().authenticated()             // alles andere geschützt
            }

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
