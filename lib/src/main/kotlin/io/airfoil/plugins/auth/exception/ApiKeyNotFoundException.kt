package io.airfoil.plugins.auth.exception

import io.airfoil.common.exception.ResourceNotFoundException
import io.airfoil.plugins.auth.data.domain.dto.ApiKeyId

class ApiKeyNotFoundException(id: ApiKeyId? = null) : ResourceNotFoundException(
    message = id?.let { "API key $it does not exist" } 
        ?: "API key does not exist",
    category = ErrorCategories.AUTH,
)
