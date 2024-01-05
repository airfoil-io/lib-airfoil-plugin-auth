package io.airfoil.plugins.auth.data.domain.dto

import io.airfoil.common.data.domain.dto.EmailAddress
import io.airfoil.common.data.domain.dto.Password
import kotlinx.serialization.Serializable

@Serializable
data class LoginUserWithPasswordRequest(
    val emailAddress: EmailAddress,
    val password: Password,
)
