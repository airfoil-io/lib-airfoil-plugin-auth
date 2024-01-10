package io.airfoil.plugins.auth.providers

import io.airfoil.plugins.auth.SessionController
import io.airfoil.plugins.auth.data.domain.dto.LoginUserWithOTPRequest
import io.airfoil.plugins.auth.extension.challengeUnauthenticated
import io.ktor.server.auth.*
import io.ktor.server.request.*

class OtpAuthProvider(
    name: String,
    private val sessionController: SessionController,
) : AuthenticationProvider(Config(name)) {

    val required: Boolean = when(name) {
        REQUIRED -> true
        else -> false
    }

    companion object {
        const val OPTIONAL: String = "auth-otp"
        const val REQUIRED: String = "auth-otp-required"
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val request = try {
            context.call.receive<LoginUserWithOTPRequest>()
        } catch (t: Throwable) {
            null
        }

        if (request == null) {
            if (required) {
                context.challengeUnauthenticated()
            }
            return
        }

        sessionController.authenticateOtp(context, request)
    }

    private class Config(name: String) : AuthenticationProvider.Config(name)
}
