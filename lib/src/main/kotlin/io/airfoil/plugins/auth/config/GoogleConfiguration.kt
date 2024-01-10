package io.airfoil.plugins.auth.config

import io.airfoil.common.extension.stringValueOrError
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class GoogleConfiguration {
    lateinit var clientId: String
    lateinit var clientSecret: String
    lateinit var redirectUri: String

    companion object {
        const val CONFIG_KEY = "google"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): GoogleConfiguration = config.config(configKey).let { cfg ->
            GoogleConfiguration().also {
                it.clientId = cfg.stringValueOrError("clientId", "Property $configKey.clientId not found")
                it.clientSecret = cfg.stringValueOrError("clientSecret", "Property $configKey.clientSecret not found")
                it.redirectUri = cfg.stringValueOrError("redirectUri", "Property $configKey.redirectUri not found")
            }
        }

        fun loadOrNull(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): GoogleConfiguration? = try {
            load(config, configKey)
        } catch (ex: ApplicationConfigurationException) {
            null
        }
    }
}
