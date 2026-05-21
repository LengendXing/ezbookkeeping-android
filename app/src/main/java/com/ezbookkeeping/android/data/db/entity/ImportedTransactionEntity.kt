package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_imported_transactions")
data class ImportedTransactionEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val source: String,
    val originalData: String,
    val status: ImportStatus = ImportStatus.PENDING
)
