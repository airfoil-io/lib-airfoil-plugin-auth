package io.airfoil.plugins.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.api.client.http.GenericUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import io.airfoil.common.data.domain.dto.EmailAddress
import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.common.data.domain.dto.Key32
import io.airfoil.common.data.domain.dto.PersonName
import io.airfoil.common.exception.InvalidConfigurationException
import io.airfoil.common.plugin.KtorApplicationPlugin
import io.airfoil.plugins.auth.authenticators.*
import io.airfoil.plugins.auth.config.SessionConfiguration
import io.airfoil.plugins.auth.data.domain.ApiKeyRepository
import io.airfoil.plugins.auth.data.domain.OtpRepository
import io.airfoil.plugins.auth.data.domain.dto.GoogleProfile
import io.airfoil.plugins.auth.data.domain.dto.LoginUserWithGoogleRequest
import io.airfoil.plugins.auth.data.domain.dto.LoginUserWithOTPRequest
import io.airfoil.plugins.auth.data.domain.dto.LoginUserWithPasswordRequest
import io.airfoil.plugins.auth.data.domain.dto.NobodyPrincipal
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.airfoil.plugins.auth.exception.ApiKeyNotFoundException
import io.airfoil.plugins.auth.exception.AuthenticationException
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
    private val authenticators: Map<String, Authenticator> = emptyMap(),
    private val apiKeyRepository: ApiKeyRepository,
    private val otpRepository: OtpRepository,
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

    private val googleOauth2HttpTransport: HttpTransport = NetHttpTransport()
    private val googleOauth2JsonFactory: JsonFactory = GsonFactory()
    private val googleIdTokenVerifier: GoogleIdTokenVerifier? = config.google?.let { googleConfig ->
        GoogleIdTokenVerifier.Builder(googleOauth2HttpTransport, googleOauth2JsonFactory)
            .setAudience(listOf(googleConfig.clientId))
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

    fun authenticateApiKey(context: AuthenticationContext, key: String): Session =
        (authenticators.get(BuiltinAuth.API_KEY_AUTH_PROVIDER_KEY) as? ApiKeyAuthenticator)?.let { authenticator ->
            try {
                val now = Clock.System.now()
                val apiKey = apiKeyRepository.fetchByApiKey(key)
                if (apiKey.active && apiKey.expiresAt > now) {
                    authenticator.authenticate(context.call, apiKey).also { session ->
                        context.call.session(session)
                        context.principal(session.toPrincipal())
                        authenticator.onAuthenticated(context.call)
                    }
                } else {
                    throw ApiKeyNotFoundException(apiKey.id)
                }
            } catch (ex: ApiKeyNotFoundException) {
                throw AuthenticationException(
                    message = "API key does not exist",
                    displayMessage = "Invalid API key",
                )
            }
        } ?: throw InvalidConfigurationException("Session controller missing ApiKey authenticator")

    fun authenticateOtp(context: AuthenticationContext, request: LoginUserWithOTPRequest): Session =
        (authenticators.get(BuiltinAuth.OTP_AUTH_PROVIDER_KEY) as? OtpAuthenticator)?.let { authenticator ->
            val now = Clock.System.now()
            val otps = otpRepository.fetchByOtp(request.otp).filter { it.active && it.expiresAt > now }
            if (otps.size > 0) {
                authenticator.authenticate(context.call, request.emailAddress, otps).also { session ->
                    context.call.session(session)
                    context.principal(session.toPrincipal())
                    authenticator.onAuthenticated(context.call)
                }
            } else {
                throw AuthenticationException(
                    message = "One-time password does not exist",
                    displayMessage = "Invalid OTP",
                )
            }
        } ?: throw InvalidConfigurationException("Session controller missing OTP authenticator")
    
    fun authenticateGoogle(context: AuthenticationContext, request: LoginUserWithGoogleRequest): Session =
        (authenticators.get(BuiltinAuth.GOOGLE_AUTH_PROVIDER_KEY) as? GoogleAuthenticator)?.let { authenticator ->
            verifyGoogleIdToken(request.idToken)?.let { idToken ->
                val payload = idToken.payload
                val profile = GoogleProfile(
                    userId = payload.subject,
                    emailAddress = EmailAddress(payload.email),
                    emailVerified = payload.emailVerified,
                    name = PersonName(
                        firstName = payload.get("given_name") as String,
                        lastName = payload.get("family_name") as String,
                    ),
                )
                authenticator.authenticate(context.call, profile).also { session ->
                    context.call.session(session)
                    context.principal(session.toPrincipal())
                    authenticator.onAuthenticated(context.call)
                }
            } ?: throw AuthenticationException(
                message = "Unable to verify Google id token",
                displayMessage = "Invalid Google Id",
            )
        } ?: throw InvalidConfigurationException("Session controller missing Google authenticator")

    private fun verifyAndDecodeJWT(jwt: String): DecodedJWT =
        jwtVerifier?.verify(jwt) ?: throw InvalidConfigurationException("Session controller missing JWT configuration")

    private fun verifyGoogleIdToken(idTokenString: String): GoogleIdToken? =
        config.google?.let { googleConfig ->
            googleIdTokenVerifier?.let { googleVerifier ->
                GoogleAuthorizationCodeTokenRequest(
                    googleOauth2HttpTransport,
                    googleOauth2JsonFactory,
                    googleConfig.clientId,
                    googleConfig.clientSecret,
                    idTokenString,
                    ""
                )
                    .setGrantType("authorization_code")
                    .setTokenServerUrl(GenericUrl("https://www.googleapis.com/oauth2/v4/token"))
                    .setRedirectUri(googleConfig.redirectUri)
                    .execute().let { tokenResponse ->
                        googleVerifier.verify(tokenResponse.idToken)
                    }
            } ?: throw InvalidConfigurationException("Session controller missing Google configuration")
        } ?: throw InvalidConfigurationException("Session controller missing Google configuration")

}
