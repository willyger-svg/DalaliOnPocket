package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val referenceNo: String,
    val amountTzs: Long,
    val payerName: String,
    val payerPhone: String,
    val paymentMethodName: String,
    val revenueSourceName: String,
    val isCreditToPlatform: Boolean,
    val paymentStatusName: String,
    val description: String,
    val efdReceiptCode: String,
    val timestamp: Long
)

@Entity(tableName = "boosted_listings")
data class BoostedListingEntity(
    @PrimaryKey val propertyId: String,
    val boostTierName: String,
    val activatedAt: Long,
    val expiresAt: Long,
    val paidAmountTzs: Long
)

@Entity(tableName = "title_verifications")
data class TitleVerificationEntity(
    @PrimaryKey val orderId: String,
    val propertyId: String,
    val propertyTitle: String,
    val applicantName: String,
    val applicantPhone: String,
    val plotNumber: String,
    val blockNumber: String,
    val titleNumber: String,
    val feeTzs: Long,
    val statusName: String,
    val registryNotes: String,
    val submittedAt: Long
)
