package io.airfoil.plugins.auth.extension

import io.airfoil.common.exception.dto.AirfoilErrorResponse
import io.airfoil.plugins.auth.exception.ErrorCategories
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationFailedCause
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond
import mu.KotlinLogging

private const val TAG = "AuthenticationContext"
private val log = KotlinLogging.logger(TAG)

fun AuthenticationContext.challengeUnauthenticated() = challenge(
    "AuthenticationProvider",
    cause = AuthenticationFailedCause.NoCredentials,
) { challenge, call ->
    log.warn { "Rejecting unauthenticated request: ${call.request.httpMethod} ${call.request.path()}" }
    call.respondUnauthorized("Unauthenticated")
    challenge.complete()
}

private suspend fun ApplicationCall.respondUnauthorized(message: String) = respond(
    HttpStatusCode.Unauthorized,
    AirfoilErrorResponse(
        category = ErrorCategories.AUTH,
        statusCode = HttpStatusCode.Unauthorized.value,
        message = message,
        displayMessage = message,
    ),
)
