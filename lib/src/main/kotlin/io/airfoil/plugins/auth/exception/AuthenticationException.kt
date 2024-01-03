package io.airfoil.plugins.auth.exception

import io.airfoil.common.exception.AirfoilException

open class AuthenticationException(
    message: String,
    displayMessage: String = message,
) : AirfoilException(
    message = message,
    displayMessage = displayMessage,
    category = ErrorCategories.AUTH,
)
