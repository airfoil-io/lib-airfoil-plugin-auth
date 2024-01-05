package io.airfoil.plugins.auth.data.domain.dto

import kotlinx.serialization.Serializable
import kotlin.runCatching

@JvmInline
@Serializable
value class AuthenticationMethod(val value: String) {
    init {
        require(value.isNotEmpty()) { "AuthenticationMethod cannot be empty" }
    }

    override fun toString() = value.toString()

    companion object {
        operator fun invoke(string: String): AuthenticationMethod? = runCatching {
            AuthenticationMethod(string)
        }.getOrNull()
    }
}
