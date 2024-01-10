package io.airfoil.plugins.auth.providers

import io.airfoil.common.data.domain.dto.Key32
import io.airfoil.common.extension.headerOrNull
import io.airfoil.plugins.auth.extension.challengeUnauthenticated
import io.airfoil.plugins.auth.SessionController
import io.ktor.server.auth.*

class ApiKeyAuthProvider(
    name: String,
    private val sessionController: SessionController,
    private val apiKeyHeader: String = DEFAULT_API_KEY_HEADER,
) : AuthenticationProvider(Config(name)) {

    val required: Boolean = when(name) {
        REQUIRED -> true
        else -> false
    }

    companion object {
        const val OPTIONAL: String = "auth-apikey"
        const val REQUIRED: String = "auth-apikey-required"

        const val DEFAULT_API_KEY_HEADER = "x-api-key"
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call

        val apiKey = call.headerOrNull<String>(apiKeyHeader).let {
            if (it.isNullOrEmpty()) {
                if (required) {
                    context.challengeUnauthenticated()
                }
                return
            }
            it
        }

        sessionController.authenticateApiKey(context, apiKey)
    }

    private class Config(name: String) : AuthenticationProvider.Config(name)
}
