package io.airfoil.plugins.auth

import io.airfoil.common.data.domain.dto.EmailAddress
import io.airfoil.common.data.domain.dto.Password
import io.airfoil.common.exception.InvalidConfigurationException
import io.airfoil.plugins.auth.config.AuthenticationConfiguration
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*

class AuthenticationController(
    private val passwordAuthenticator: PasswordAuthenticator? = null,
    private val config: AuthenticationConfiguration,
) {

    fun loginUserWithPassword(call: ApplicationCall, emailAddress: EmailAddress, password: Password): Session =
        passwordAuthenticator?.let {
            it(call, emailAddress, password)
        } ?: throw InvalidConfigurationException("Session controller missing password authenticator")

}
