package io.airfoil.plugins.auth

import io.airfoil.common.extension.withLogMetadata
import io.airfoil.common.plugin.createKtorApplicationPlugin
import io.airfoil.plugins.auth.authenticators.Authenticator
import io.airfoil.plugins.auth.config.SessionConfiguration
import io.airfoil.plugins.auth.database.*
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
    var authenticators: Map<String, Authenticator> = emptyMap()
    lateinit var config: SessionConfiguration
}

fun Application.configureSession(
    authenticators: Map<String, Authenticator> = emptyMap(),
) {
    install(SessionControllerPlugin) {
        this.authenticators = authenticators
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
            authenticators = pluginConfig.authenticators,
            apiKeyRepository = application.apiKeyRepository,
            otpRepository = application.otpRepository,
            config = pluginConfig.config,
        )
    )

    application.sessionController
}
