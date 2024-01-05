package io.airfoil.plugins.auth.extension

import io.airfoil.plugins.auth.data.domain.dto.Session
import io.ktor.server.application.*
import io.ktor.util.*

private val sessionKey = AttributeKey<Session>("Session")

fun ApplicationCall.session(session: Session) =
    attributes.put(sessionKey, session)

val ApplicationCall.session: Session
    get() = attributes[sessionKey]
