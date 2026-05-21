package com.ezbookkeeping.android.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ezbk_users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val nickname: String,
    val avatar: String? = null,
    val defaultAccountId: Int? = null,
    val baseCurrency: String = "USD",
    val timezone: String = "UTC",
    val dateFormat: String = "yyyy-MM-dd",
    val currencyFormat: String = "0.00",
    val decimalSeparator: String = ".",
    val digitGroupingSymbol: String = ",",
    val groupSeparator: String = ",",
    val firstDayOfWeek: Int = 1,
    val monthlyStartDate: Int = 1,
    val expenseAmountColor: String = "#FF0000",
    val incomeAmountColor: String = "#00FF00",
    val isLocked: Boolean = false,
    val lockType: LockType = LockType.NONE,
    val lockCode: String? = null,
    val twoFactorSecret: String? = null,
    val role: UserRole = UserRole.USER
)
