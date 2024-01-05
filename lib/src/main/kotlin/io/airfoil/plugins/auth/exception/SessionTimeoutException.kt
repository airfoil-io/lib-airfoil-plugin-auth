package io.airfoil.plugins.auth.exception

import io.airfoil.common.exception.AirfoilException

class SessionTimeoutException(message: String? = null) : AirfoilException(
    message = message ?: "Authentication token timeout",
    displayMessage = "Session timeout",
    category = ErrorCategories.SESSION,
)
