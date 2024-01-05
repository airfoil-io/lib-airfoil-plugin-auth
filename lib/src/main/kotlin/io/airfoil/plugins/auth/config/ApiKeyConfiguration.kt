package io.airfoil.plugins.auth.config

import io.airfoil.common.extension.stringValueOrDefault
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class ApiKeyConfiguration {
    lateinit var header: String

    companion object {
        const val CONFIG_KEY = "apiKey"

        const val DEFAULT_HEADER = "x-api-key"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): ApiKeyConfiguration = config.config(configKey).let { cfg ->
            ApiKeyConfiguration().also {
                it.header = cfg.stringValueOrDefault("header", DEFAULT_HEADER)
            }
        }

        fun loadOrNull(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): ApiKeyConfiguration? = try {
            load(config, configKey)
        } catch (ex: ApplicationConfigurationException) {
            null
        }
    }
}
