package io.airfoil.plugins.auth.data.domain

import io.airfoil.common.data.domain.dto.Otp6
import io.airfoil.common.data.domain.dto.Otp8
import io.airfoil.plugins.auth.data.domain.dto.Otp
import io.airfoil.plugins.auth.data.domain.dto.OtpId
import io.airfoil.plugins.auth.exception.OtpNotFoundException

interface OtpRepository {
    // create a new OTP
    fun create(otp: Otp): Otp

    // delete an OTP
    fun delete(id: OtpId): Otp

    // revoke an OTP
    fun revoke(id: OtpId): Otp

    // fetch an OTP by id
    fun fetchOrNull(id: OtpId): Otp?
    fun fetch(id: OtpId): Otp =
        fetchOrNull(id) ?: throw OtpNotFoundException(id)

    // fetch an OTP by the password
    fun fetchByOtp(otp: String): List<Otp>

    // fetch an OTP by 6-character password
    fun fetchByOtp6(otp: Otp6): List<Otp> =
        fetchByOtp(otp.value)
    
    // fetch an OTP by 8-character password
    fun fetchByOtp8(otp: Otp8): List<Otp> =
        fetchByOtp(otp.value)
}
