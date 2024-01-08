package io.airfoil.plugins.auth.authenticators

import com.auth0.jwt.interfaces.DecodedJWT
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*

interface JwtAuthenticator : Authenticator {
    fun authenticate(call: ApplicationCall, jwt: DecodedJWT): Session
}
