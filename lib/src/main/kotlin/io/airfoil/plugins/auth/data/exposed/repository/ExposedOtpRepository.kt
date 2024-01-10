package io.airfoil.plugins.auth.data.exposed.repository

import io.airfoil.plugins.auth.data.domain.OtpRepository
import io.airfoil.plugins.auth.data.domain.dto.Otp
import io.airfoil.plugins.auth.data.domain.dto.OtpId
import io.airfoil.plugins.auth.data.exposed.table.OtpRecord
import io.airfoil.plugins.auth.data.exposed.table.OtpTable
import io.airfoil.plugins.auth.exception.OtpNotFoundException
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.Clock

class ExposedOtpRepository : OtpRepository {
    override fun create(otp: Otp): Otp = transaction {
        OtpRecord.new(otp.id.value) {
            this.active = otp.active
            this.otp = otp.otpToString()
            this.createdAt = otp.createdAt
            this.updatedAt = otp.updatedAt
            this.expiresAt = otp.expiresAt
        }.toDTO()
    }

    override fun delete(id: OtpId): Otp = transaction {
        OtpRecord.findById(id.value)?.let {
            it.delete()
            it.toDTO()
        } ?: throw OtpNotFoundException(id)
    }

    override fun revoke(id: OtpId): Otp = transaction {
        val record = OtpRecord.findById(id.value)
            ?: throw OtpNotFoundException(id)

        record.apply {
            active = false
        }

        record.toDTO()
    }

    override fun fetchOrNull(id: OtpId): Otp? = transaction {
        OtpRecord.findById(id.value)?.toDTO()
    }

    override fun fetchByOtp(otp: String): List<Otp> = transaction {
        OtpTable.findByOTP(otp).map { it.toDTO() }
    }
}
