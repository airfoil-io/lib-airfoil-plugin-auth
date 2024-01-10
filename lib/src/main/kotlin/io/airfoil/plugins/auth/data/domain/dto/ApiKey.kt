package io.airfoil.plugins.auth.data.domain.dto

import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.common.data.domain.dto.Key32
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ApiKey(
    val id: ApiKeyId = ApiKeyId.random(),
    val active: Boolean = true,
    val key16: Key16? = null,
    val key32: Key32? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = createdAt,
    val expiresAt: Instant,
) {

    init {
        require(key16 != null || key32 != null) { "API key requires either 16-byte or 32-byte key" }
    }

    fun keyToString(): String = if (key16 != null) {
        key16.value
    } else if (key32 != null) {
        key32.value
    } else {
        throw IllegalArgumentException("API key requires either 16-byte or 32-byte key")
    }

}
