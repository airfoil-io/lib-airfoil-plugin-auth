package io.airfoil.plugins.auth

import io.airfoil.common.exception.InvalidConfigurationException
import io.airfoil.plugins.auth.config.SessionConfiguration
import io.airfoil.plugins.auth.providers.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import mu.KotlinLogging

private const val TAG = "BuiltinAuthProviders"
private val log = KotlinLogging.logger(TAG)

typealias AuthProviderConfigurator = (application: Application) -> List<AuthenticationProvider>
typealias AuthProviderSessionConfigValidator = (sessionConfig: SessionConfiguration?) -> Unit

data class BuiltinAuthProvider(
    val configurator: AuthProviderConfigurator,
    val configValidator: AuthProviderSessionConfigValidator,
)

object BuiltinAuth {
    const val PASSWORD_AUTH_PROVIDER_KEY: String = "password"
    const val JWT_AUTH_PROVIDER_KEY: String = "jwt"
    const val API_KEY_16_AUTH_PROVIDER_KEY: String = "apikey-16"
    const val API_KEY_32_AUTH_PROVIDER_KEY: String = "apikey-32"

    val Keys: List<String> = listOf(
        PASSWORD_AUTH_PROVIDER_KEY,
        JWT_AUTH_PROVIDER_KEY,
        API_KEY_16_AUTH_PROVIDER_KEY,
        API_KEY_32_AUTH_PROVIDER_KEY,
    )

    val Providers: Map<String, BuiltinAuthProvider> = mapOf(
        PASSWORD_AUTH_PROVIDER_KEY to BuiltinAuthProvider(
            configurator = { application ->
                log.info("Configuring JWT authentication provider")
                listOf(
                    PasswordAuthProvider(
                        name = PasswordAuthProvider.REQUIRED,
                        sessionController = application.sessionController,
                    ),
                    PasswordAuthProvider(
                        name = PasswordAuthProvider.OPTIONAL,
                        sessionController = application.sessionController,
                    ),
                )
            },
            configValidator = { _ ->
            }
        ),
        JWT_AUTH_PROVIDER_KEY to BuiltinAuthProvider(
            configurator = { application ->
                log.info("Configuring JWT authentication provider")
                listOf(
                    JwtAuthProvider(
                        name = JwtAuthProvider.REQUIRED,
                        sessionController = application.sessionController,
                    ),
                    JwtAuthProvider(
                        name = JwtAuthProvider.OPTIONAL,
                        sessionController = application.sessionController,
                    ),
                )
            },
            configValidator = { sessionConfig ->
                if (sessionConfig?.jwt == null) {
                    throw InvalidConfigurationException("Missing session.jwt config for 'jwt' provider")
                }
            }
        ),
        API_KEY_16_AUTH_PROVIDER_KEY to BuiltinAuthProvider(
            configurator = { application ->
                log.info("Configuring 16-byte API key authentication provider")
                listOf(
                    ApiKey16AuthProvider(
                        name = ApiKey16AuthProvider.REQUIRED,
                        sessionController = application.sessionController,
                        apiKeyHeader = application.authConfig.session?.apiKey?.header 
                            ?: ApiKey16AuthProvider.DEFAULT_API_KEY_HEADER,
                    ),
                    ApiKey16AuthProvider(
                        name = ApiKey16AuthProvider.OPTIONAL,
                        sessionController = application.sessionController,
                        apiKeyHeader = application.authConfig.session?.apiKey?.header 
                            ?: ApiKey16AuthProvider.DEFAULT_API_KEY_HEADER,
                    ),
                )
            },
            configValidator = { _ ->
            }
        ),
        API_KEY_32_AUTH_PROVIDER_KEY to BuiltinAuthProvider(
            configurator = { application ->
                log.info("Configuring 32-byte API key authentication provider")
                listOf(
                    ApiKey32AuthProvider(
                        name = ApiKey32AuthProvider.REQUIRED,
                        sessionController = application.sessionController,
                        apiKeyHeader = application.authConfig.session?.apiKey?.header 
                            ?: ApiKey32AuthProvider.DEFAULT_API_KEY_HEADER,
                    ),
                    ApiKey32AuthProvider(
                        name = ApiKey32AuthProvider.OPTIONAL,
                        sessionController = application.sessionController,
                        apiKeyHeader = application.authConfig.session?.apiKey?.header 
                            ?: ApiKey32AuthProvider.DEFAULT_API_KEY_HEADER,
                    ),
                )
            },
            configValidator = { _ ->
            }
        ),
    )
}
