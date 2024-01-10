package io.airfoil.plugins.auth

import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.common.data.domain.dto.Key32
import io.airfoil.common.data.domain.dto.Otp6
import io.airfoil.common.data.domain.dto.Otp8
import io.airfoil.common.exception.InvalidConfigurationException
import io.airfoil.plugins.auth.config.AuthenticationConfiguration
import io.airfoil.plugins.auth.data.domain.ApiKeyRepository
import io.airfoil.plugins.auth.data.domain.OtpRepository
import io.airfoil.plugins.auth.data.domain.dto.ApiKey
import io.airfoil.plugins.auth.data.domain.dto.ApiKeyId
import io.airfoil.plugins.auth.data.domain.dto.Otp
import io.airfoil.plugins.auth.data.domain.dto.OtpId
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*
import kotlin.time.Duration
import kotlinx.datetime.Clock

class AuthenticationController(
    private val sessionController: SessionController,
    private val apiKeyRepository: ApiKeyRepository,
    private val otpRepository: OtpRepository,
    private val config: AuthenticationConfiguration,
) {

    fun generateOtp6(
        validFor: Duration,
        alphaNumeric: Boolean = false,
    ): Otp =
        Clock.System.now().let { now ->
            otpRepository.create(
                Otp(
                    otp6 = when (alphaNumeric) {
                        true -> Otp6.randomAlphaNumeric()
                        false -> Otp6.randomNumeric()
                    },
                    createdAt = now,
                    expiresAt = now + validFor,
                )
            )
        }
    
    fun generateOtp8(
        validFor: Duration,
        alphaNumeric: Boolean = false,
    ): Otp =
        Clock.System.now().let { now ->
            otpRepository.create(
                Otp(
                    otp8 = when (alphaNumeric) {
                        true -> Otp8.randomAlphaNumeric()
                        false -> Otp8.randomNumeric()
                    },
                    createdAt = now,
                    expiresAt = now + validFor,
                )
            )
        }
    
    fun revokeOtp(id: OtpId) =
        otpRepository.revoke(id)
    
    fun generateApiKey16(validFor: Duration): ApiKey =
        Clock.System.now().let { now ->
            apiKeyRepository.create(
                ApiKey(
                    key16 = Key16.random(),
                    createdAt = now,
                    expiresAt = now + validFor,
                )
            )
        }
    
    fun generateApiKey32(validFor: Duration): ApiKey =
        Clock.System.now().let { now ->
            apiKeyRepository.create(
                ApiKey(
                    key32 = Key32.random(),
                    createdAt = now,
                    expiresAt = now + validFor,
                )
            )
        }
    
    fun revokeApiKey(id: ApiKeyId) =
        apiKeyRepository.revoke(id)

}
