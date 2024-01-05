package io.airfoil.plugins.auth.data.exposed.repository

import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.common.data.domain.dto.Key32
import io.airfoil.plugins.auth.data.domain.ApiKeyRepository
import io.airfoil.plugins.auth.data.domain.dto.ApiKey
import io.airfoil.plugins.auth.data.domain.dto.ApiKeyId
import io.airfoil.plugins.auth.data.exposed.table.ApiKeyRecord
import io.airfoil.plugins.auth.data.exposed.table.ApiKeyTable
import io.airfoil.plugins.auth.exception.ApiKeyNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.Clock

class ExposedApiKeyRepository : ApiKeyRepository {
    override fun create(apiKey: ApiKey): ApiKey = transaction {
        ApiKeyRecord.new(apiKey.id.value) {
            this.active = apiKey.active
            this.apiKey = apiKey.keyToString()
            this.createdAt = apiKey.createdAt
            this.updatedAt = apiKey.updatedAt
        }.toDTO()
    }

    override fun delete(id: ApiKeyId): ApiKey = transaction {
        ApiKeyRecord.findById(id.value)?.let {
            it.delete()
            it.toDTO()
        } ?: throw ApiKeyNotFoundException(id)
    }

    override fun revoke(id: ApiKeyId): ApiKey = transaction {
        val record = ApiKeyRecord.findById(id.value)
            ?: throw ApiKeyNotFoundException(id)

        record.apply {
            active = false
        }

        record.toDTO()
    }

    override fun fetchOrNull(id: ApiKeyId): ApiKey? = transaction {
        ApiKeyRecord.findById(id.value)?.toDTO()
    }

    override fun fetchByApiKey16OrNull(key: Key16): ApiKey? = transaction {
        ApiKeyTable.findByAPIKey(key.value)?.toDTO()
    }

    override fun fetchByApiKey32OrNull(key: Key32): ApiKey? = transaction {
        ApiKeyTable.findByAPIKey(key.value)?.toDTO()
    }
}
