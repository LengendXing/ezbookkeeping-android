package com.ezbookkeeping.android.data.db.converter

import androidx.room.TypeConverter
import com.ezbookkeeping.android.data.db.entity.*
import java.util.Date

class Converters {
    @TypeConverter fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }
    @TypeConverter fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter fun fromAccountType(value: AccountType): String = value.name
    @TypeConverter fun toAccountType(value: String): AccountType = AccountType.valueOf(value)

    @TypeConverter fun fromTransactionType(value: TransactionType): String = value.name
    @TypeConverter fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter fun fromCategoryType(value: CategoryType): String = value.name
    @TypeConverter fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)

    @TypeConverter fun fromTokenRecordType(value: TokenRecordType): String = value.name
    @TypeConverter fun toTokenRecordType(value: String): TokenRecordType = TokenRecordType.valueOf(value)

    @TypeConverter fun fromImportStatus(value: ImportStatus): String = value.name
    @TypeConverter fun toImportStatus(value: String): ImportStatus = ImportStatus.valueOf(value)

    @TypeConverter fun fromLockType(value: LockType): String = value.name
    @TypeConverter fun toLockType(value: String): LockType = LockType.valueOf(value)

    @TypeConverter fun fromUserRole(value: UserRole): String = value.name
    @TypeConverter fun toUserRole(value: String): UserRole = UserRole.valueOf(value)

    @TypeConverter fun fromIntList(value: List<Int>): String = value.joinToString(",")
    @TypeConverter fun toIntList(value: String): List<Int> =
        if (value.isBlank()) emptyList() else value.split(",").map { it.trim().toInt() }

    @TypeConverter fun fromStringList(value: List<String>): String = value.joinToString("||")
    @TypeConverter fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("||").map { it.trim() }
}
