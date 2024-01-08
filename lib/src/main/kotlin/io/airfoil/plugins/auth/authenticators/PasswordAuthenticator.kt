package io.airfoil.plugins.auth.authenticators

import io.airfoil.common.data.domain.dto.EmailAddress
import io.airfoil.common.data.domain.dto.Password
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*

interface PasswordAuthenticator : Authenticator {
    fun authenticate(call: ApplicationCall, emailAddress: EmailAddress, password: Password): Session
}
