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
    const val API_KEY_AUTH_PROVIDER_KEY: String = "apikey"
    const val OTP_AUTH_PROVIDER_KEY: String = "otp"
    const val GOOGLE_AUTH_PROVIDER_KEY: String = "google"

    val Keys: List<String> = listOf(
        PASSWORD_AUTH_PROVIDER_KEY,
        JWT_AUTH_PROVIDER_KEY,
        API_KEY_AUTH_PROVIDER_KEY,
        OTP_AUTH_PROVIDER_KEY,
        GOOGLE_AUTH_PROVIDER_KEY,
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

        API_KEY_AUTH_PROVIDER_KEY to BuiltinAuthProvider(
            configurator = { application ->
                log.info("Configuring API key authentication provider")
                listOf(
                    ApiKeyAuthProvider(
                        name = ApiKeyAuthProvider.REQUIRED,
                        sessionController = application.sessionController,
                        apiKeyHeader = application.authConfig.session?.apiKey?.requestHeader 
                            ?: ApiKeyAuthProvider.DEFAULT_API_KEY_HEADER,
                    ),
                    ApiKeyAuthProvider(
                        name = ApiKeyAuthProvider.OPTIONAL,
                        sessionController = application.sessionController,
                        apiKeyHeader = application.authConfig.session?.apiKey?.requestHeader 
                            ?: ApiKeyAuthProvider.DEFAULT_API_KEY_HEADER,
                    ),
                )
            },
            configValidator = { _ ->
            }
        ),

        OTP_AUTH_PROVIDER_KEY to BuiltinAuthProvider(
            configurator = { application ->
                log.info("Configuring OTP key authentication provider")
                listOf(
                    OtpAuthProvider(
                        name = OtpAuthProvider.REQUIRED,
                        sessionController = application.sessionController,
                    ),
                    OtpAuthProvider(
                        name = OtpAuthProvider.OPTIONAL,
                        sessionController = application.sessionController,
                    ),
                )
            },
            configValidator = { _ ->
            }
        ),

        GOOGLE_AUTH_PROVIDER_KEY to BuiltinAuthProvider(
            configurator = { application ->
                log.info("Configuring Google authentication provider")
                listOf(
                    GoogleAuthProvider(
                        name = GoogleAuthProvider.REQUIRED,
                        sessionController = application.sessionController,
                    ),
                    GoogleAuthProvider(
                        name = GoogleAuthProvider.OPTIONAL,
                        sessionController = application.sessionController,
                    ),
                )
            },
            configValidator = { _ ->
            }
        ),
    )
}
