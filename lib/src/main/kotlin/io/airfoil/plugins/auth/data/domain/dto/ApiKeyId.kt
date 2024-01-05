package io.airfoil.plugins.auth.data.domain.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID
import kotlin.runCatching

@JvmInline
@Serializable(with = ApiKeyIdSerializer::class)
value class ApiKeyId(val value: UUID) {
    override fun toString() = value.toString()

    companion object {
        fun random() = ApiKeyId(UUID.randomUUID())

        operator fun invoke(string: String): ApiKeyId? = runCatching {
            ApiKeyId(UUID.fromString(string))
        }.getOrNull()
    }

}

object ApiKeyIdSerializer : KSerializer<ApiKeyId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.airfoil.plugins.auth.data.domain.dto.ApiKeyIdSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ApiKeyId =
        ApiKeyId(UUID.fromString(decoder.decodeString()))

    override fun serialize(encoder: Encoder, value: ApiKeyId) {
        encoder.encodeString(value.value.toString())
    }
}
