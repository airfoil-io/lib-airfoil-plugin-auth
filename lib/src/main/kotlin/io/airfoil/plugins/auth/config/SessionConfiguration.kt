package io.airfoil.plugins.auth.config

import io.airfoil.common.config.JwtConfiguration
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class SessionConfiguration {
    var jwt: JwtConfiguration? = null
    var apiKey: ApiKeyConfiguration? = null

    companion object {
        const val CONFIG_KEY = "session"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): SessionConfiguration = config.config(configKey).let { cfg ->
            SessionConfiguration().also {
                it.jwt = JwtConfiguration.loadOrNull(cfg, "jwt")
                it.apiKey = ApiKeyConfiguration.loadOrNull(cfg, "apiKey")
            }
        }

        fun loadOrNull(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): SessionConfiguration? = try {
            load(config, configKey)
        } catch (ex: ApplicationConfigurationException) {
            null
        }
    }
}
