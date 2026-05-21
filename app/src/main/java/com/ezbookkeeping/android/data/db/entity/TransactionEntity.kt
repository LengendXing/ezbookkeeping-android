package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_transactions")
data class TransactionEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val sourceAccountId: Int,
    val destinationAccountId: Int? = null,
    val sourceAmount: Double,
    val destinationAmount: Double? = null,
    val type: TransactionType,
    val categoryId: Int? = null,
    val tagIds: List<Int> = emptyList(),
    val templateId: Int? = null,
    val comment: String? = null,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val date: String,
    val time: String? = null
)
