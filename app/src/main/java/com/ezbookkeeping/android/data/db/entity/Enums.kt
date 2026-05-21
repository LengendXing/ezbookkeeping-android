package com.ezbookkeeping.android.data.db.entity

enum class AccountType { ASSET, LIABILITY }
enum class TransactionType { EXPENSE, INCOME, TRANSFER }
enum class CategoryType { EXPENSE, INCOME, TRANSFER }
enum class TokenRecordType { ACCESS, REFRESH }
enum class ImportStatus { PENDING, PROCESSING, COMPLETED, FAILED }
enum class LockType { NONE, PIN, PASSWORD, BIOMETRIC }
enum class UserRole { ADMIN, USER }
