package io.airfoil.plugins.auth.data.domain.dto

import io.airfoil.common.data.domain.dto.Otp6
import io.airfoil.common.data.domain.dto.Otp8
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Otp(
    val id: OtpId = OtpId.random(),
    val active: Boolean = true,
    val otp6: Otp6? = null,
    val otp8: Otp8? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = createdAt,
    val expiresAt: Instant,
) {

    init {
        require(otp6 != null || otp8 != null) { "OTP requires either 6-character or 8-character one-time password" }
    }

    fun otpToString(): String = if (otp6 != null) {
        otp6.value
    } else if (otp8 != null) {
        otp8.value
    } else {
        throw IllegalArgumentException("OTP requires either 6-character or 8-character one-time password")
    }

}
