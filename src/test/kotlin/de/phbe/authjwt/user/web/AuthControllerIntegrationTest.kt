package de.phbe.authjwt.user.web

import de.phbe.authjwt.user.web.dto.LoginRequest
import de.phbe.authjwt.user.web.dto.RegisterRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import tools.jackson.module.kotlin.jacksonObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest @Autowired constructor(
    val mockMvc: MockMvc
) : FunSpec({
    val objectMapper = jacksonObjectMapper()

    test("Registrierung und Login liefern JWT-Tokens") {
        val email = "integrationtest@example.com"
        val password = "testpass123"
        val registerRequest = RegisterRequest(email, password)
        val loginRequest = LoginRequest(email, password)

        // Registrierung
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect { status { isOk() } }

        // Login
        val loginResult = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect { status { isOk() } }
            .andReturn()

        val responseBody = loginResult.response.contentAsString
        val tokens = objectMapper.readTree(responseBody)
        tokens.has("accessToken") shouldBe true
        tokens.has("refreshToken") shouldBe true
    }

    test("Login mit falschem Passwort liefert Fehler") {
        val loginRequest = LoginRequest("integrationtest@example.com", "falsch")
        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect { status { is4xxClientError() } }
    }
})
