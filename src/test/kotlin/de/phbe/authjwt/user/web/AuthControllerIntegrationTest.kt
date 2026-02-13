package de.phbe.authjwt.user.web

import de.phbe.authjwt.user.web.dto.LoginRequest
import de.phbe.authjwt.user.web.dto.RegisterRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import tools.jackson.module.kotlin.jacksonObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest : FunSpec(){

    @Autowired
    lateinit var mockMvc: MockMvc

    val objectMapper = jacksonObjectMapper()

    init{
        extension(SpringExtension())

        test("Registrierung und Login liefern JWT-Tokens") {
            val email = "integrationtest@example.com"
            val password = "testpass123"
            val registerRequest = RegisterRequest(email, password)
            val loginRequest = LoginRequest(email, password)

            // Registrierung
            val registerResult = mockMvc.post("/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(registerRequest)
            }.andExpect {
                status { isOk() }
            }.andReturn()

            // Login
            val loginResult = mockMvc.post("/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(loginRequest)
            }.andExpect { status { isOk() } }
                .andReturn()

            val responseBodyRegister = registerResult.response.contentAsString
            val responseBodyLogin = loginResult.response.contentAsString
            val tokensRegister = objectMapper.readTree(responseBodyRegister)
            val tokensLogin = objectMapper.readTree(responseBodyLogin)
            tokensRegister.has("accessToken") shouldBe true
            tokensRegister.has("refreshToken") shouldBe true
            tokensLogin.has("accessToken") shouldBe true
            tokensLogin.has("refreshToken") shouldBe true
        }

        test("Login mit falschem Passwort liefert Fehler") {
            val loginRequest = LoginRequest("integrationtest@example.com", "falsch")
            mockMvc.post("/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(loginRequest)
            }.andExpect { status { is4xxClientError() } }
        }
    }
}
