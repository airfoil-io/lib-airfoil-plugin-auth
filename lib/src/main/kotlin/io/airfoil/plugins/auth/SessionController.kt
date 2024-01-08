package io.airfoil.plugins.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.common.data.domain.dto.Key32
import io.airfoil.common.exception.InvalidConfigurationException
import io.airfoil.common.plugin.KtorApplicationPlugin
import io.airfoil.plugins.auth.authenticators.*
import io.airfoil.plugins.auth.config.SessionConfiguration
import io.airfoil.plugins.auth.data.domain.dto.LoginUserWithPasswordRequest
import io.airfoil.plugins.auth.data.domain.dto.NobodyPrincipal
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.airfoil.plugins.auth.exception.InvalidJWTException
import io.airfoil.plugins.auth.exception.SessionTimeoutException
import io.airfoil.plugins.auth.extension.session
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Date
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import mu.KotlinLogging

private const val TAG = "SessionController"
private val log = KotlinLogging.logger(TAG)

class SessionController(
    var authenticators: Map<String, Authenticator> = emptyMap(),
    private val config: SessionConfiguration,
) : KtorApplicationPlugin {

    private val jwtAlgorithm: Algorithm? = config.jwt?.let { jwtConfig ->
        KeyFactory.getInstance("RSA").let { keyFactory ->
            Algorithm.RSA256(
                keyFactory.generatePublic(
                    X509EncodedKeySpec(Base64.getDecoder().decode(jwtConfig.publicKey))
                ) as RSAPublicKey,
                keyFactory.generatePrivate(
                    PKCS8EncodedKeySpec(Base64.getDecoder().decode(jwtConfig.privateKey))
                ) as RSAPrivateKey,
            )
        }
    }
    private val jwtVerifier: JWTVerifier? = config.jwt?.let { jwtConfig ->
        JWT.require(jwtAlgorithm)
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .build()
    }

    override fun onCallRespond(call: ApplicationCall) {
        config.token?.also { tokenConfig ->
            config.jwt?.also { jwtConfig ->
                tokenConfig.responseHeader?.also { responseHeader ->
                    val isNobody = call.principal<NobodyPrincipal>()?.let { true } ?: false
                    if (!isNobody) {
                        val callSession: Session? = try {
                            call.session
                        } catch (t: Throwable) {
                            null
                        }
                        callSession?.also { session ->
                            val jwt = JWT.create().let { jwtBuilder ->
                                jwtBuilder.withSubject(session.subject)
                                jwtBuilder.withAudience(jwtConfig.audience)
                                jwtBuilder.withIssuer(jwtConfig.issuer)
                                if (tokenConfig.autoRefresh) {
                                    val now = Clock.System.now()
                                    jwtBuilder.withIssuedAt(Date.from(now.toJavaInstant()))
                                    jwtBuilder.withExpiresAt(
                                        when(jwtConfig.neverExpires) {
                                            true -> Date(Long.Companion.MAX_VALUE)
                                            else -> Date.from((now + jwtConfig.expiration).toJavaInstant())
                                        }
                                    )
                                } else {
                                    jwtBuilder.withIssuedAt(Date.from(session.startedAt.toJavaInstant()))
                                    jwtBuilder.withExpiresAt(
                                        when(jwtConfig.neverExpires) {
                                            true -> Date(Long.Companion.MAX_VALUE)
                                            else -> Date.from((session.startedAt + jwtConfig.expiration).toJavaInstant())
                                        }
                                    )
                                }
                                session.getClaims().forEach {
                                    jwtBuilder.withClaim(it.key, it.value)
                                }
                                jwtBuilder.sign(jwtAlgorithm)
                            }
                            call.response.headers.append(responseHeader, jwt)
                        }
                    }
                }
            }
        }
    }

    fun authenticatePassword(context: AuthenticationContext, request: LoginUserWithPasswordRequest): Session =
        (authenticators.get(BuiltinAuth.PASSWORD_AUTH_PROVIDER_KEY) as? PasswordAuthenticator)?.let { authenticator ->
            authenticator.authenticate(context.call, request.emailAddress, request.password).also { session ->
                context.call.session(session)
                context.principal(session.toPrincipal())
                authenticator.onAuthenticated(context.call)
            }
        } ?: throw InvalidConfigurationException("Session controller missing password authenticator")

    fun authenticateJWT(context: AuthenticationContext, jwt: String): Session =
        try {
            verifyAndDecodeJWT(jwt).let { decodedJwt ->
                (authenticators.get(BuiltinAuth.JWT_AUTH_PROVIDER_KEY) as? JwtAuthenticator)?.let { authenticator ->
                    authenticator.authenticate(context.call, decodedJwt).also { session ->
                        context.call.session(session)
                        context.principal(session.toPrincipal())
                        authenticator.onAuthenticated(context.call)
                    }
                } ?: throw InvalidConfigurationException("Session controller missing JWT authenticator")
            }
        } catch (ex: TokenExpiredException) {
            throw SessionTimeoutException(ex.message)
        } catch (ex: JWTVerificationException) {
            throw InvalidJWTException(ex.message)
        }
    
    fun authenticateApiKey16(context: AuthenticationContext, key: Key16): Session =
        (authenticators.get(BuiltinAuth.API_KEY_16_AUTH_PROVIDER_KEY) as? ApiKey16Authenticator)?.let { authenticator ->
            authenticator.authenticate(context.call, key).also { session ->
                context.call.session(session)
                context.principal(session.toPrincipal())
                authenticator.onAuthenticated(context.call)
            }
        } ?: throw InvalidConfigurationException("Session controller missing ApiKey16 authenticator")
    
    fun authenticateApiKey32(context: AuthenticationContext, key: Key32): Session =
        (authenticators.get(BuiltinAuth.API_KEY_32_AUTH_PROVIDER_KEY) as? ApiKey32Authenticator)?.let { authenticator ->
            authenticator.authenticate(context.call, key).also { session ->
                context.call.session(session)
                context.principal(session.toPrincipal())
                authenticator.onAuthenticated(context.call)
            }
        } ?: throw InvalidConfigurationException("Session controller missing ApiKey32 authenticator")

    private fun verifyAndDecodeJWT(jwt: String): DecodedJWT =
        jwtVerifier?.verify(jwt) ?: throw InvalidConfigurationException("Session controller missing JWT configuration")

}
