package io.airfoil.plugins.auth.data.domain.dto

import io.ktor.server.auth.Principal
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

abstract class Session {
    open val authMethod: AuthenticationMethod = AuthenticationMethods.UNKNOWN
    abstract val subject: String
    open val startedAt: Instant = Clock.System.now()

    abstract fun toPrincipal(): Principal
    abstract fun getClaims(): Map<String, String>
}
