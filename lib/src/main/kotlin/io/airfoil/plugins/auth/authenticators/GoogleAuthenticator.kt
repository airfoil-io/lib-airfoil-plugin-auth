package io.airfoil.plugins.auth.authenticators

import io.airfoil.plugins.auth.data.domain.dto.GoogleProfile
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*

interface GoogleAuthenticator : Authenticator {
    fun authenticate(call: ApplicationCall, profile: GoogleProfile): Session
}
