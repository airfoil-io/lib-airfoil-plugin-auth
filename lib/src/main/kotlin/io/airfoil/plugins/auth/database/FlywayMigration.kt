package io.airfoil.plugins.auth.database

import io.ktor.server.application.*
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.ClassicConfiguration

private const val TAG = "AuthenticationFlywayMigration"
private val log = KotlinLogging.logger(TAG)

private const val AUTH_DATA_MIGRATION_LOCATION = "classpath:db/auth/migration"

fun Application.configureAuthFlywayMigration(
    dbUrl: String,
    dbUsername: String,
    dbPassword: String,
) {
    log.info("Performing authentication flyway database migration")

    val flywayConfig = Flyway
        .configure()
        .dataSource(dbUrl, dbUsername, dbPassword)
        .table("_flyway_auth")
        .load()
        .getConfiguration() as ClassicConfiguration
    flywayConfig.setLocationsAsStrings(*listOf(AUTH_DATA_MIGRATION_LOCATION).toTypedArray())

    Flyway(flywayConfig).also {
        it.baseline()
        it.migrate()
    }
}
