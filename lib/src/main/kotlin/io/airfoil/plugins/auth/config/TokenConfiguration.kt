package io.airfoil.plugins.auth.config

import io.airfoil.common.extension.boolValueOrDefault
import io.airfoil.common.extension.stringValueOrNull
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class TokenConfiguration {
    var autoRefresh: Boolean = DEFAULT_AUTO_REFRESH
    var responseHeader: String? = null

    companion object {
        const val CONFIG_KEY = "token"

        const val DEFAULT_AUTO_REFRESH = false

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): TokenConfiguration = config.config(configKey).let { cfg ->
            TokenConfiguration().also {
                it.autoRefresh = cfg.boolValueOrDefault("autoRefresh", DEFAULT_AUTO_REFRESH)
                it.responseHeader = cfg.stringValueOrNull("responseHeader")
            }
        }

        fun loadOrNull(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): TokenConfiguration? = try {
            load(config, configKey)
        } catch (ex: ApplicationConfigurationException) {
            null
        }
    }
}
