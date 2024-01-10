package io.airfoil.plugins.auth.authenticators

import io.airfoil.plugins.auth.data.domain.dto.ApiKey
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*

interface ApiKeyAuthenticator : Authenticator {
    fun authenticate(call: ApplicationCall, key: ApiKey): Session
}
