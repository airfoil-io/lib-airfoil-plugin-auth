package io.airfoil.plugins.auth.exception

class InvalidJWTException(message: String? = null) : AuthenticationException(
    message = message ?: "Failed to parse authentication token",
    displayMessage = "Invalid authentication token",
)
