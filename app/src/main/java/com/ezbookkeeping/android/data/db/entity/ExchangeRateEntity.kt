package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ezbk_exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey val id: Int,
    val currency: String,
    val rate: Double,
    val source: String,
    val updateTime: Date? = null
)
