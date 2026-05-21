package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_accounts")
data class AccountEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val type: AccountType,
    val name: String,
    val icon: String,
    val color: String,
    val currency: String,
    val balance: Double = 0.0,
    val creditLimit: Double = 0.0,
    val initialBalance: Double = 0.0,
    val isCounting: Boolean = true,
    val order: Int = 0
)
