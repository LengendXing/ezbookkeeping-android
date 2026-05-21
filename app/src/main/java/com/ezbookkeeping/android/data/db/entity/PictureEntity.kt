package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_pictures")
data class PictureEntity(
    @PrimaryKey val id: Int,
    val transactionId: Int,
    val originalName: String,
    val mimeType: String,
    val fileSize: Long,
    val storagePath: String
)
