package io.airfoil.plugins.auth.data.domain.dto

import io.airfoil.common.data.domain.dto.EmailAddress
import io.airfoil.common.data.domain.dto.PersonName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleProfile(
    val userId: String,
    val emailAddress: EmailAddress,
    val emailVerified: Boolean,
    val name: PersonName,
)
