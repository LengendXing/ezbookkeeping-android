package com.ezbookkeeping.android.data.remote.api

import com.ezbookkeeping.android.data.remote.dto.*
import retrofit2.http.*

interface EZBookkeepingApi {
    // Auth
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): ApiResponse<UserDto>

    @POST("api/v1/auth/logout")
    suspend fun logout()

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<LoginResponse>

    // User
    @GET("api/v1/user")
    suspend fun getCurrentUser(): ApiResponse<UserDto>

    @PUT("api/v1/user")
    suspend fun updateUser(@Body request: UpdateUserRequest): ApiResponse<UserDto>

    // Accounts
    @GET("api/v1/accounts")
    suspend fun getAccounts(): ApiResponse<List<AccountDto>>

    @POST("api/v1/accounts")
    suspend fun createAccount(@Body request: CreateAccountRequest): ApiResponse<AccountDto>

    @PUT("api/v1/accounts/{id}")
    suspend fun updateAccount(@Path("id") id: Int, @Body request: CreateAccountRequest): ApiResponse<AccountDto>

    @DELETE("api/v1/accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: Int)

    // Transactions
    @GET("api/v1/transactions")
    suspend fun getTransactions(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<List<TransactionDto>>

    @POST("api/v1/transactions")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): ApiResponse<TransactionDto>

    @PUT("api/v1/transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: Int, @Body request: CreateTransactionRequest): ApiResponse<TransactionDto>

    @DELETE("api/v1/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int)

    // Categories
    @GET("api/v1/categories")
    suspend fun getCategories(): ApiResponse<List<CategoryDto>>

    @POST("api/v1/categories")
    suspend fun createCategory(@Body request: CreateCategoryRequest): ApiResponse<CategoryDto>

    @PUT("api/v1/categories/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body request: CreateCategoryRequest): ApiResponse<CategoryDto>

    @DELETE("api/v1/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int)

    // Tags
    @GET("api/v1/tags")
    suspend fun getTags(): ApiResponse<List<TagGroupDto>>

    // Templates
    @GET("api/v1/transaction-templates")
    suspend fun getTemplates(): ApiResponse<List<TemplateDto>>

    // Exchange Rates
    @GET("api/v1/exchange-rates")
    suspend fun getExchangeRates(): ApiResponse<List<ExchangeRateDto>>

    // Statistics
    @GET("api/v1/statistics")
    suspend fun getStatistics(
        @Query("type") type: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): ApiResponse<StatisticsDto>
}
