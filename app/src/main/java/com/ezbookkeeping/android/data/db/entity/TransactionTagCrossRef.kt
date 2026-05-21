package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity

@Entity(
    tableName = "ezbk_transaction_tag_cross_ref",
    primaryKeys = ["transactionId", "tagId"]
)
data class TransactionTagCrossRef(
    val transactionId: Int,
    val tagId: Int
)
