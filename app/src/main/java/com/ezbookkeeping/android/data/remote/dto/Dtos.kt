package com.ezbookkeeping.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null
)

@Serializable
data class LoginRequest(val username: String, val password: String, val twoFactorCode: String? = null)

@Serializable
data class SignupRequest(
    val username: String, val password: String, val email: String,
    val nickname: String? = null, val defaultAccountId: Int? = null
)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class LoginResponse(val accessToken: String, val refreshToken: String, val expiresIn: Long)

@Serializable
data class UserDto(
    val id: Int, val username: String, val email: String, val nickname: String,
    val avatar: String? = null, val defaultAccountId: Int? = null,
    val baseCurrency: String = "USD", val timezone: String = "UTC",
    val dateFormat: String = "yyyy-MM-dd", val currencyFormat: String = "0.00",
    val expenseAmountColor: String = "#FF0000", val incomeAmountColor: String = "#00FF00",
    val role: String = "USER"
)

@Serializable
data class UpdateUserRequest(
    val nickname: String? = null, val email: String? = null,
    val baseCurrency: String? = null, val timezone: String? = null,
    val dateFormat: String? = null, val defaultAccountId: Int? = null
)

@Serializable
data class AccountDto(
    val id: Int, val type: String, val name: String, val icon: String,
    val color: String, val currency: String, val balance: Double = 0.0,
    val creditLimit: Double = 0.0, val initialBalance: Double = 0.0,
    val isCounting: Boolean = true, val order: Int = 0
)

@Serializable
data class CreateAccountRequest(
    val type: String, val name: String, val icon: String, val color: String,
    val currency: String, val initialBalance: Double = 0.0,
    val creditLimit: Double = 0.0, val isCounting: Boolean = true
)

@Serializable
data class TransactionDto(
    val id: Int, val sourceAccountId: Int, val destinationAccountId: Int? = null,
    val sourceAmount: Double, val destinationAmount: Double? = null,
    val type: String, val categoryId: Int? = null, val tagIds: List<Int> = emptyList(),
    val comment: String? = null, val date: String, val time: String? = null
)

@Serializable
data class CreateTransactionRequest(
    val sourceAccountId: Int, val destinationAccountId: Int? = null,
    val sourceAmount: Double, val destinationAmount: Double? = null,
    val type: String, val categoryId: Int? = null, val tagIds: List<Int> = emptyList(),
    val comment: String? = null, val date: String, val time: String? = null
)

@Serializable
data class CategoryDto(
    val id: Int, val type: String, val parentId: Int? = null,
    val name: String, val icon: String, val color: String,
    val order: Int = 0, val isHidden: Boolean = false
)

@Serializable
data class CreateCategoryRequest(
    val type: String, val parentId: Int? = null, val name: String,
    val icon: String, val color: String
)

@Serializable
data class TagGroupDto(val id: Int, val name: String, val order: Int = 0, val tags: List<TagDto> = emptyList())

@Serializable
data class TagDto(val id: Int, val name: String)

@Serializable
data class TemplateDto(
    val id: Int, val name: String, val amount: Double, val type: String,
    val sourceAccountId: Int, val destinationAccountId: Int? = null,
    val categoryId: Int? = null, val tagIds: List<Int> = emptyList(),
    val comment: String? = null, val order: Int = 0
)

@Serializable
data class ExchangeRateDto(val id: Int, val currency: String, val rate: Double, val source: String, val updateTime: String? = null)

@Serializable
data class StatisticsDto(
    val totalExpense: Double = 0.0, val totalIncome: Double = 0.0,
    val categories: List<CategoryStatisticDto> = emptyList()
)

@Serializable
data class CategoryStatisticDto(val categoryId: Int, val categoryName: String, val amount: Double, val percentage: Double)
