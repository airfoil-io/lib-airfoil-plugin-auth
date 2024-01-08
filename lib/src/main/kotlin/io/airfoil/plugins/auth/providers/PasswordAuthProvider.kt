package io.airfoil.plugins.auth.providers

import io.airfoil.plugins.auth.SessionController
import io.airfoil.plugins.auth.data.domain.dto.LoginUserWithPasswordRequest
import io.airfoil.plugins.auth.extension.challengeUnauthenticated
import io.airfoil.plugins.auth.extension.session
import io.ktor.server.auth.*
import io.ktor.server.request.*

class PasswordAuthProvider(
    name: String,
    private val sessionController: SessionController,
) : AuthenticationProvider(Config(name)) {

    val required: Boolean = when(name) {
        REQUIRED -> true
        else -> false
    }

    companion object {
        const val OPTIONAL: String = "auth-password"
        const val REQUIRED: String = "auth-password-required"
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val request = try {
            context.call.receive<LoginUserWithPasswordRequest>()
        } catch (t: Throwable) {
            null
        }

        if (request == null) {
            if (required) {
                context.challengeUnauthenticated()
            }
            return
        }

        sessionController.authenticatePassword(context, request)
    }

    private class Config(name: String) : AuthenticationProvider.Config(name)
}
