package io.airfoil.plugins.auth.exception

class InvalidOTPException(message: String? = null) : AuthenticationException(
    message = message ?: "Invalid OTP for user",
    displayMessage = "Invalid OTP",
)
