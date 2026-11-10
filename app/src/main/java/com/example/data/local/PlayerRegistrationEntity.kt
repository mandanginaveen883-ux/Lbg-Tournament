package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.PaymentStatus
import com.example.data.model.RegistrationStatus

@Entity(
    tableName = "registrations",
    indices = [
        Index(value = ["tournamentId", "freeFireUid"], unique = true)
    ]
)
data class PlayerRegistrationEntity(
    @PrimaryKey
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val playerName: String,
    val freeFireUid: String,
    val inGameName: String,
    val mobileNumber: String,
    val teamName: String? = null,
    val teammateUids: String? = null,
    val entryFee: Int = 0,
    val registrationStatus: RegistrationStatus = RegistrationStatus.AWAITING_PAYMENT,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val utrNumber: String? = null,
    val paymentProofNote: String? = null,
    val paymentExpiresAt: Long = System.currentTimeMillis() + 10 * 60 * 1000L, // 10 minutes
    val registeredAt: Long = System.currentTimeMillis(),
    val verifiedAt: Long? = null,
    val rejectionReason: String? = null,
    val assignedSlotNumber: Int? = null,
    val isTestPayment: Boolean = false
)
