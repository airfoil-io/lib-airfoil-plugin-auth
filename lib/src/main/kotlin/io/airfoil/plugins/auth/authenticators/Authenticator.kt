package io.airfoil.plugins.auth.authenticators

import io.ktor.server.application.*

interface Authenticator {
    fun onAuthenticated(call: ApplicationCall)
}
