package io.airfoil.plugins.auth.data.exposed.table

import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.common.data.domain.dto.Key32
import io.airfoil.plugins.auth.data.domain.dto.ApiKey
import io.airfoil.plugins.auth.data.domain.dto.ApiKeyId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

object ApiKeyTable : UUIDTable(
    name = "api_key",
    columnName = "id",
) {
    val active = bool("active")
    val apiKey = text("api_key")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    fun findByAPIKey(apiKey: String) =
        ApiKeyTable.select { ApiKeyTable.apiKey eq apiKey }
            .toList().firstOrNull()?.toApiKeyRecord()

    fun entityOf(id: UUID) = EntityID<UUID>(id, ApiKeyTable)
}

private fun ResultRow.toApiKeyRecord() = ApiKeyRecord.wrapRow(this)

class ApiKeyRecord(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ApiKeyRecord>(ApiKeyTable)

    var active by ApiKeyTable.active
    var apiKey by ApiKeyTable.apiKey
    var createdAt by ApiKeyTable.createdAt
    var updatedAt by ApiKeyTable.updatedAt

    fun toDTO(): ApiKey = ApiKey(
        id = ApiKeyId(id.value),
        active = active,
        key16 = when (apiKey.length) {
            16 -> Key16(apiKey)
            else -> null
        },
        key32 = when (apiKey.length) {
            32 -> Key32(apiKey)
            else -> null
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
