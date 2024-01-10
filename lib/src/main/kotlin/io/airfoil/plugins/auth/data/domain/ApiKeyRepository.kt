package io.airfoil.plugins.auth.data.domain

import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.common.data.domain.dto.Key32
import io.airfoil.plugins.auth.data.domain.dto.ApiKey
import io.airfoil.plugins.auth.data.domain.dto.ApiKeyId
import io.airfoil.plugins.auth.exception.ApiKeyNotFoundException

interface ApiKeyRepository {
    // create a new API key
    fun create(apiKey: ApiKey): ApiKey

    // delete an API key
    fun delete(id: ApiKeyId): ApiKey

    // revoke an API key
    fun revoke(id: ApiKeyId): ApiKey

    // fetch an API key by id
    fun fetchOrNull(id: ApiKeyId): ApiKey?
    fun fetch(id: ApiKeyId): ApiKey =
        fetchOrNull(id) ?: throw ApiKeyNotFoundException(id)
    
    // fetch an API key by the key
    fun fetchByApiKeyOrNull(key: String): ApiKey?
    fun fetchByApiKey(key: String): ApiKey =
        fetchByApiKeyOrNull(key) ?: throw ApiKeyNotFoundException()

    // fetch an API key by the 16-byte api key
    fun fetchByApiKey16OrNull(key: Key16): ApiKey?
        = fetchByApiKeyOrNull(key.value)
    fun fetchByApiKey16(key: Key16): ApiKey =
        fetchByApiKey16OrNull(key) ?: throw ApiKeyNotFoundException()
    
    // fetch an API key by the 32-byte api key
    fun fetchByApiKey32OrNull(key: Key32): ApiKey?
        = fetchByApiKeyOrNull(key.value)
    fun fetchByApiKey32(key: Key32): ApiKey =
        fetchByApiKey32OrNull(key) ?: throw ApiKeyNotFoundException()
}
