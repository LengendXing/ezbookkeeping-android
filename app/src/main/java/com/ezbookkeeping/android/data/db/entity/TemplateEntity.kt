package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_templates")
data class TemplateEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val name: String,
    val amount: Double,
    val type: TransactionType,
    val sourceAccountId: Int,
    val destinationAccountId: Int? = null,
    val categoryId: Int? = null,
    val tagIds: List<Int> = emptyList(),
    val comment: String? = null,
    val order: Int = 0
)
