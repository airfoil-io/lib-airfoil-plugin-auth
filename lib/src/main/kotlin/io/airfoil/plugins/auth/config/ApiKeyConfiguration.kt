package io.airfoil.plugins.auth.config

import io.airfoil.common.extension.stringValueOrDefault
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class ApiKeyConfiguration {
    var requestHeader: String = DEFAULT_REQUEST_HEADER

    companion object {
        const val CONFIG_KEY = "apiKey"

        const val DEFAULT_REQUEST_HEADER = "x-api-key"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): ApiKeyConfiguration = config.config(configKey).let { cfg ->
            ApiKeyConfiguration().also {
                it.requestHeader = cfg.stringValueOrDefault("requestHeader", DEFAULT_REQUEST_HEADER)
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
