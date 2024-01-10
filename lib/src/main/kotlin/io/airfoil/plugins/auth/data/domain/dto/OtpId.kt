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
@Serializable(with = OtpIdSerializer::class)
value class OtpId(val value: UUID) {
    override fun toString() = value.toString()

    companion object {
        fun random() = OtpId(UUID.randomUUID())

        operator fun invoke(string: String): OtpId? = runCatching {
            OtpId(UUID.fromString(string))
        }.getOrNull()
    }

}

object OtpIdSerializer : KSerializer<OtpId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.airfoil.plugins.auth.data.domain.dto.OtpIdSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OtpId =
        OtpId(UUID.fromString(decoder.decodeString()))

    override fun serialize(encoder: Encoder, value: OtpId) {
        encoder.encodeString(value.value.toString())
    }
}
