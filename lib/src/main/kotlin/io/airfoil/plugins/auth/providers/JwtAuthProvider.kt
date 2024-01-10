package io.airfoil.plugins.auth.providers

import io.airfoil.common.extension.parseBearerToken
import io.airfoil.plugins.auth.extension.challengeUnauthenticated
import io.airfoil.plugins.auth.SessionController
import io.ktor.server.auth.*

class JwtAuthProvider(
    name: String,
    private val sessionController: SessionController,
) : AuthenticationProvider(Config(name)) {

    val required: Boolean = when(name) {
        REQUIRED -> true
        else -> false
    }

    companion object {
        const val OPTIONAL: String = "auth-jwt"
        const val REQUIRED: String = "auth-jwt-required"
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val request = context.call.request

        val bearerToken = request.parseBearerToken()
        if (bearerToken == null) {
            if (required) {
                context.challengeUnauthenticated()
            }
            return
        }

        sessionController.authenticateJWT(context, bearerToken)
    }

    private class Config(name: String) : AuthenticationProvider.Config(name)
}
