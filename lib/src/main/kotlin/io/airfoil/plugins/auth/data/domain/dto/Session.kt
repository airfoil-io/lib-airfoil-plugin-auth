package io.airfoil.plugins.auth.data.domain.dto

import io.ktor.server.auth.Principal

abstract class Session {
    open val authMethod: AuthenticationMethod = AuthenticationMethods.UNKNOWN
    abstract fun toPrincipal(): Principal
}
