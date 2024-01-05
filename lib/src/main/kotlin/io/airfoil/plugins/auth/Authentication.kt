package io.airfoil.plugins.auth

import io.airfoil.common.exception.InvalidConfigurationException
import io.airfoil.plugins.auth.config.AuthenticationConfiguration
import io.airfoil.plugins.auth.database.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.*
import mu.KotlinLogging

private const val TAG = "AuthenticationControllerPlugin"
private val log = KotlinLogging.logger(TAG)

val authConfigAttrKey = AttributeKey<AuthenticationConfiguration>("AuthenticationConfiguration")

val Application.authConfig: AuthenticationConfiguration
    get() = attributes[authConfigAttrKey]

fun Application.authConfig(authConfig: AuthenticationConfiguration) {
    attributes.put(authConfigAttrKey, authConfig)
}

fun Application.loadAuthenticationConfig(): AuthenticationConfiguration =
    AuthenticationConfiguration.load(environment.config).also {
        authConfig(it)
    }

val authenticationControllerAttrKey = AttributeKey<AuthenticationController>("AuthenticationController")

val Application.authenticationController: AuthenticationController
    get() = attributes[authenticationControllerAttrKey]

fun Application.authenticationController(authenticationController: AuthenticationController) {
    attributes.put(authenticationControllerAttrKey, authenticationController)
}

private val authProvidersKey = AttributeKey<List<String>>("AuthProviders")

val Application.authProviders: Array<out String>
    get() = attributes[authProvidersKey].toTypedArray()

private class AuthenticationControllerArguments {
    var passwordAuthenticator: PasswordAuthenticator? = null
    lateinit var config: AuthenticationConfiguration
}

fun Application.configureAuthentication(
    dbUrl: String,
    dbUsername: String,
    dbPassword: String,
    apiKey16Authenticator: ApiKey16Authenticator? = null,
    apiKey32Authenticator: ApiKey32Authenticator? = null,
    jwtAuthenticator: JwtAuthenticator? = null,
    passwordAuthenticator: PasswordAuthenticator? = null,
    providers: List<AuthenticationProvider> = emptyList(),
) {
    loadAuthenticationConfig()
    configureAuthFlywayMigration(dbUrl, dbUsername, dbPassword)
    configureAuthRepositories()
    configureSession(
        apiKey16Authenticator,
        apiKey32Authenticator,
        jwtAuthenticator,
        passwordAuthenticator,
    )

    val application = this
    val allAuthProviders: List<AuthenticationProvider> = buildList {
        val unknownProviders: List<String> = authConfig.providers.toMutableList().also {
            it.removeAll { it in BuiltinAuth.Keys }
        }.toList()

        if (unknownProviders.size > 0) {
            throw InvalidConfigurationException("Unknown authentication providers [${unknownProviders.joinToString()}] ")
        }

        authConfig.providers.forEach {
            val provider = BuiltinAuth.Providers.get(it)!!
            provider.configValidator(authConfig.session)
            addAll(provider.configurator(application))
        }

        addAll(providers)
    }
    attributes.put(authProvidersKey, allAuthProviders.map { it.name }.filterNotNull())

    install(Authentication) {
        allAuthProviders.forEach { provider ->
            register(provider)
        }
    }

    install(AuthenticationControllerPlugin) {
        this.passwordAuthenticator = passwordAuthenticator
        config = authConfig
    }
}

private val AuthenticationControllerPlugin = createApplicationPlugin(
    name = "Authentication Controller Plugin",
    createConfiguration = ::AuthenticationControllerArguments,
) {
    log.info { "Configuring authentication controller" }

    application.authenticationController(
        AuthenticationController(
            passwordAuthenticator = pluginConfig.passwordAuthenticator,
            config = pluginConfig.config,
        )
    )
}
