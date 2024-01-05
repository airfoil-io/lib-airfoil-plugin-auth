package io.airfoil.plugins.auth.exception

class InvalidPasswordException(message: String? = null) : AuthenticationException(
    message = message ?: "Invalid password for user",
    displayMessage = "Invalid password",
)
