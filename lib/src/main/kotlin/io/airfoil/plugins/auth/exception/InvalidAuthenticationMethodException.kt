package io.airfoil.plugins.auth.exception

import io.airfoil.plugins.auth.data.domain.dto.AuthenticationMethod

class InvalidAuthenticationMethodException(method: AuthenticationMethod) : AuthenticationException(
    message = "Invalid authentication method $method for user",
    displayMessage = "Invalid authentication method",
)
