package io.airfoil.plugins.auth.data.exposed.table

import io.airfoil.common.data.domain.dto.Otp6
import io.airfoil.common.data.domain.dto.Otp8
import io.airfoil.plugins.auth.data.domain.dto.Otp
import io.airfoil.plugins.auth.data.domain.dto.OtpId
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

object OtpTable : UUIDTable(
    name = "otp",
    columnName = "id",
) {
    val active = bool("active")
    val otp = text("otp")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val expiresAt = timestamp("expires_at")

    fun findByOTP(otp: String) =
        OtpTable.select { OtpTable.otp eq otp }
            .toList().map { it.toOTPRecord() }

    fun entityOf(id: UUID) = EntityID<UUID>(id, OtpTable)
}

private fun ResultRow.toOTPRecord() = OtpRecord.wrapRow(this)

class OtpRecord(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<OtpRecord>(OtpTable)

    var active by OtpTable.active
    var otp by OtpTable.otp
    var createdAt by OtpTable.createdAt
    var updatedAt by OtpTable.updatedAt
    var expiresAt by OtpTable.expiresAt

    fun toDTO(): Otp = Otp(
        id = OtpId(id.value),
        active = active,
        otp6 = when (otp.length) {
            6 -> Otp6(otp)
            else -> null
        },
        otp8 = when (otp.length) {
            8 -> Otp8(otp)
            else -> null
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
        expiresAt = expiresAt,
    )
}
