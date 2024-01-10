package io.airfoil.plugins.auth.exception

import io.airfoil.common.exception.ResourceNotFoundException
import io.airfoil.plugins.auth.data.domain.dto.OtpId

class OtpNotFoundException(id: OtpId? = null) : ResourceNotFoundException(
    message = id?.let { "OTP $it does not exist" } 
        ?: "OTP does not exist",
    category = ErrorCategories.AUTH,
)
