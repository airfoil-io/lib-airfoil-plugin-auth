package io.airfoil.plugins.auth.data.domain.dto

object AuthenticationMethods {
    val UNKNOWN = AuthenticationMethod("UNKNOWN")
    val API_KEY = AuthenticationMethod("API_KEY")
    val PASSWORD = AuthenticationMethod("PASSWORD")
    val JWT = AuthenticationMethod("JWT")
    val OTP = AuthenticationMethod("OTP")
    val GOOGLE = AuthenticationMethod("GOOGLE")
}
