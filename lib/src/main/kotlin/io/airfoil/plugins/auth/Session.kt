package io.airfoil.plugins.auth

import io.airfoil.common.extension.withLogMetadata
import io.airfoil.common.plugin.createKtorApplicationPlugin
import io.airfoil.plugins.auth.config.SessionConfiguration
import io.ktor.server.application.*
import io.ktor.util.*
import mu.KotlinLogging

private const val TAG = "SessionControllerPlugin"
private val log = KotlinLogging.logger(TAG)

val sessionControllerAttrKey = AttributeKey<SessionController>("SessionController")

val Application.sessionController: SessionController
    get() = attributes[sessionControllerAttrKey]

fun Application.sessionController(sessionController: SessionController) {
    attributes.put(sessionControllerAttrKey, sessionController)
}

private class SessionControllerArguments {
    var apiKey16Authenticator: ApiKey16Authenticator? = null
    var apiKey32Authenticator: ApiKey32Authenticator? = null
    var jwtAuthenticator: JwtAuthenticator? = null
    var passwordAuthenticator: PasswordAuthenticator? = null
    lateinit var config: SessionConfiguration
}

fun Application.configureSession(
    apiKey16Authenticator: ApiKey16Authenticator? = null,
    apiKey32Authenticator: ApiKey32Authenticator? = null,
    jwtAuthenticator: JwtAuthenticator? = null,
    passwordAuthenticator: PasswordAuthenticator? = null,
) {
    install(SessionControllerPlugin) {
        this.apiKey16Authenticator = apiKey16Authenticator
        this.apiKey32Authenticator = apiKey32Authenticator
        this.jwtAuthenticator = jwtAuthenticator
        this.passwordAuthenticator = passwordAuthenticator
        config = authConfig.session ?: SessionConfiguration()
    }
}

private val SessionControllerPlugin = createKtorApplicationPlugin(
    name = "Session Controller Plugin",
    createConfiguration = ::SessionControllerArguments,
) {
    log.info("Configuring session controller")

    pluginConfig.config.jwt?.also {
        log.info {
            "Configuring JWT session".withLogMetadata(
                "jwt.publicKey" to it.publicKey,
                "jwt.privateKey" to "<SECRET>",
                "jwt.issuer" to it.issuer,
                "jwt.audience" to it.audience,
                "jwt.expiration" to it.expiration,
                "jwt.neverExpires" to it.neverExpires,
            )
        }
    }

    application.sessionController(
        SessionController(
            apiKey16Authenticator = pluginConfig.apiKey16Authenticator,
            apiKey32Authenticator = pluginConfig.apiKey32Authenticator,
            jwtAuthenticator = pluginConfig.jwtAuthenticator,
            passwordAuthenticator = pluginConfig.passwordAuthenticator,
            config = pluginConfig.config,
        )
    )

    application.sessionController
}
