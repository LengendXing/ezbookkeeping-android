package com.ezbookkeeping.android.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ezbookkeeping.android.data.db.converter.Converters
import com.ezbookkeeping.android.data.db.dao.*
import com.ezbookkeeping.android.data.db.entity.*

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        TagEntity::class,
        TagGroupEntity::class,
        TemplateEntity::class,
        PictureEntity::class,
        ExchangeRateEntity::class,
        TokenEntity::class,
        ImportedTransactionEntity::class,
        ExternalAuthEntity::class,
        CloudSettingEntity::class,
        TransactionTagCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun templateDao(): TemplateDao
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun tokenDao(): TokenDao
    abstract fun pictureDao(): PictureDao
    abstract fun importedTransactionDao(): ImportedTransactionDao
    abstract fun externalAuthDao(): ExternalAuthDao
    abstract fun cloudSettingDao(): CloudSettingDao
}
