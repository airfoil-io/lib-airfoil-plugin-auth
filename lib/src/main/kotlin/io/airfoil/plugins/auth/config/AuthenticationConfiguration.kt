package io.airfoil.plugins.auth.config

import io.airfoil.common.exception.InvalidConfigurationException
import io.airfoil.common.extension.stringListOrEmpty
import io.ktor.server.config.ApplicationConfig

class AuthenticationConfiguration {
    var providers: List<String> = emptyList()
    var session: SessionConfiguration? = null

    companion object {
        const val CONFIG_KEY = "auth"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): AuthenticationConfiguration = config.config(configKey).let { cfg ->
            AuthenticationConfiguration().also {
                it.providers = cfg.stringListOrEmpty("providers")
                it.session = SessionConfiguration.loadOrNull(cfg)
            }.also {
                it.validate()
            }
        }
    }

    fun validate() {
        // TODO
    }
}
