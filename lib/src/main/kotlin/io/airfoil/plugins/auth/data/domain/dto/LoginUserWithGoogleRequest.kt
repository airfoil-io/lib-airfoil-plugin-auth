package io.airfoil.plugins.auth.data.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserWithGoogleRequest(
    val idToken: String,
)
