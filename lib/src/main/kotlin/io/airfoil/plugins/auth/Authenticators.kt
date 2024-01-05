package io.airfoil.plugins.auth

import com.auth0.jwt.interfaces.DecodedJWT
import io.airfoil.common.data.domain.dto.EmailAddress
import io.airfoil.common.data.domain.dto.Key16
import io.airfoil.common.data.domain.dto.Key32
import io.airfoil.common.data.domain.dto.Password
import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*

typealias ApiKey16Authenticator = (call: ApplicationCall, key: Key16) -> Session
typealias ApiKey32Authenticator = (call: ApplicationCall, key: Key32) -> Session
typealias JwtAuthenticator = (call: ApplicationCall, jwt: DecodedJWT) -> Session
typealias PasswordAuthenticator = (call: ApplicationCall, emailAddress: EmailAddress, password: Password) -> Session
