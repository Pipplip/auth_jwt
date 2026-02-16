package de.phbe.authjwt.exception

import de.phbe.authjwt.user.domain.exception.InvalidCredentialsException
import de.phbe.authjwt.user.domain.exception.InvalidRefreshTokenException
import de.phbe.authjwt.user.domain.exception.UnauthorizedException
import de.phbe.authjwt.user.domain.exception.UserAlreadyExistsException
import de.phbe.authjwt.user.domain.exception.UserNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

// Fangt alle Exceptions ab, die in Controllern auftreten
// Übersetzt sie in HTTP-Statuscodes + DTOs für die Response

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String?,
    val validationErrors: Map<String, String>? = null
)

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException) =
        buildResponse(ex.message, HttpStatus.CONFLICT)

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException) =
        buildResponse(ex.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException) =
        buildResponse(ex.message, HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException) =
        buildResponse(ex.message, HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleUnauthorized(ex: InvalidRefreshTokenException) =
        buildResponse(ex.message, HttpStatus.CONFLICT)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors
            .associate { it.field to (it.defaultMessage ?: "Invalid value") }

        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Validation failed",
            path = request.requestURI,
            validationErrors = fieldErrors
        )

        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        // Logge die Exception (z.B. mit Logger, hier nur printStackTrace)
        ex.printStackTrace()

        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "Unexpected server error",
            path = request.requestURI
        )
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun buildResponse(
        message: String?,
        status: HttpStatus
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                path = null
            ),
            status
        )
    }
}