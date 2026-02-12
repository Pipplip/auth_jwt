package de.phbe.authjwt.user.web

import de.phbe.authjwt.user.web.dto.LoginRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import tools.jackson.module.kotlin.jacksonObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSecurityTest @Autowired constructor(
    val mockMvc: MockMvc
) : FunSpec({
    val objectMapper = jacksonObjectMapper()
    val email = "securitytest@example.com"
    val password = "testpass123"

    beforeTest {
        // Registrierung und Login, um Token zu erhalten
        val registerRequest = de.phbe.authjwt.user.web.dto.RegisterRequest(email, password)
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }
    }

    test("Zugriff auf /users/profile ohne Token wird abgelehnt") {
        mockMvc.get("/users/profile")
            .andExpect { status { is4xxClientError() } }
    }

    test("Zugriff auf /users/profile mit manipuliertem Token wird abgelehnt") {
        mockMvc.get("/users/profile") {
            header("Authorization", "Bearer invalid.token.value")
        }.andExpect { status { is4xxClientError() } }
    }

    test("Zugriff auf /users/profile mit gültigem Token ist erlaubt") {
        // Login, um Token zu erhalten
        val loginRequest = LoginRequest(email, password)
        val loginResult = mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andReturn()
        val responseBody = loginResult.response.contentAsString
        val accessToken = objectMapper.readTree(responseBody).get("accessToken").asText()

        val profileResult = mockMvc.get("/users/profile") {
            header("Authorization", "Bearer $accessToken")
        }
            .andExpect { status { isOk() } }
            .andReturn()

        profileResult.response.contentAsString.contains(email) shouldBe true
    }
})
