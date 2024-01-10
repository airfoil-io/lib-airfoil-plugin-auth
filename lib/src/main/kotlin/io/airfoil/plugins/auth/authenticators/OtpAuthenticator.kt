package io.airfoil.plugins.auth.authenticators

import io.airfoil.common.data.domain.dto.EmailAddress
import io.airfoil.plugins.auth.data.domain.dto.Otp
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*

interface OtpAuthenticator : Authenticator {
    fun authenticate(call: ApplicationCall, emailAddress: EmailAddress, otps: List<Otp>): Session
}
