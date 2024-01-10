package io.airfoil.plugins.auth.data.domain.dto

import io.airfoil.common.data.domain.dto.EmailAddress
import kotlinx.serialization.Serializable

@Serializable
data class LoginUserWithOTPRequest(
    val emailAddress: EmailAddress,
    val otp: String,
)
