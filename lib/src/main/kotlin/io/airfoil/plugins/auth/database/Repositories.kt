package io.airfoil.plugins.auth.database

import io.airfoil.plugins.auth.data.domain.ApiKeyRepository
import io.airfoil.plugins.auth.data.domain.OtpRepository
import io.airfoil.plugins.auth.data.exposed.repository.*
import io.ktor.server.application.*
import io.ktor.util.*

fun Application.configureAuthRepositories() {
    apiKeyRepository(ExposedApiKeyRepository())
    otpRepository(ExposedOtpRepository())
}

private val apiKeyRepositoryAttrKey = AttributeKey<ApiKeyRepository>("ApiKeyRepository")

val Application.apiKeyRepository: ApiKeyRepository
    get() = attributes[apiKeyRepositoryAttrKey]

fun Application.apiKeyRepository(apiKeyRepository: ApiKeyRepository) {
    attributes.put(apiKeyRepositoryAttrKey, apiKeyRepository)
}

private val otpRepositoryAttrKey = AttributeKey<OtpRepository>("OtpRepository")

val Application.otpRepository: OtpRepository
    get() = attributes[otpRepositoryAttrKey]

fun Application.otpRepository(otpRepository: OtpRepository) {
    attributes.put(otpRepositoryAttrKey, otpRepository)
}
