package io.airfoil.plugins.auth.authenticators

import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*

interface ApiKey16Authenticator : Authenticator {
    fun authenticate(call: ApplicationCall, key: Key16): Session
}
