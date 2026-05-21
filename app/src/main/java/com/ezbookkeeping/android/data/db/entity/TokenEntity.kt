package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ezbk_tokens")
data class TokenEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val token: String,
    val type: TokenRecordType,
    val expireTime: Date? = null
)
