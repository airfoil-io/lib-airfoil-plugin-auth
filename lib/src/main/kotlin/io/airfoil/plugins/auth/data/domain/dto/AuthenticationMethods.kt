package io.airfoil.plugins.auth.data.domain.dto

object AuthenticationMethods {
    val UNKNOWN = AuthenticationMethod("UNKNOWN")
    val PASSWORD = AuthenticationMethod("PASSWORD")
    val JWT = AuthenticationMethod("JWT")
}
