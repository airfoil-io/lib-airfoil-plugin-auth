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
import io.airfoil.plugins.auth.config.SessionConfiguration
import io.airfoil.plugins.auth.data.domain.dto.LoginUserWithPasswordRequest
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.airfoil.plugins.auth.exception.InvalidJWTException
import io.airfoil.plugins.auth.exception.SessionTimeoutException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import mu.KotlinLogging

private const val TAG = "SessionController"
private val log = KotlinLogging.logger(TAG)

class SessionController(
    private val apiKey16Authenticator: ApiKey16Authenticator? = null,
    private val apiKey32Authenticator: ApiKey32Authenticator? = null,
    private val jwtAuthenticator: JwtAuthenticator? = null,
    private val passwordAuthenticator: PasswordAuthenticator? = null,
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
        // TODO
    }

    fun authenticatePassword(context: AuthenticationContext, request: LoginUserWithPasswordRequest): Session =
        passwordAuthenticator?.let {
            it(context.call, request.emailAddress, request.password)
        } ?: throw InvalidConfigurationException("Session controller missing password authenticator")

    fun authenticateJWT(context: AuthenticationContext, jwt: String): Session =
        try {
            verifyAndDecodeJWT(jwt).let { decodedJwt ->
                jwtAuthenticator?.let {
                    it(context.call, decodedJwt)
                } ?: throw InvalidConfigurationException("Session controller missing JWT authenticator")
            }
        } catch (ex: TokenExpiredException) {
            throw SessionTimeoutException(ex.message)
        } catch (ex: JWTVerificationException) {
            throw InvalidJWTException(ex.message)
        }
    
    fun authenticateApiKey16(context: AuthenticationContext, key: Key16): Session =
        apiKey16Authenticator?.let {
            it(context.call, key)
        } ?: throw InvalidConfigurationException("Session controller missing ApiKey16 authenticator")
    
    fun authenticateApiKey32(context: AuthenticationContext, key: Key32): Session =
        apiKey32Authenticator?.let {
            it(context.call, key)
        } ?: throw InvalidConfigurationException("Session controller missing ApiKey32 authenticator")

    private fun verifyAndDecodeJWT(jwt: String): DecodedJWT =
        jwtVerifier?.verify(jwt) ?: throw InvalidConfigurationException("Session controller missing JWT configuration")

}
