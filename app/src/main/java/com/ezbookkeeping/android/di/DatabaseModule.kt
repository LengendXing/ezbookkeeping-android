package com.ezbookkeeping.android.di

import android.content.Context
import androidx.room.Room
import com.ezbookkeeping.android.data.db.AppDatabase
import com.ezbookkeeping.android.data.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "ezbk_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
    @Provides fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()
    @Provides fun provideTemplateDao(db: AppDatabase): TemplateDao = db.templateDao()
    @Provides fun provideExchangeRateDao(db: AppDatabase): ExchangeRateDao = db.exchangeRateDao()
    @Provides fun provideTokenDao(db: AppDatabase): TokenDao = db.tokenDao()
    @Provides fun providePictureDao(db: AppDatabase): PictureDao = db.pictureDao()
    @Provides fun provideImportedTransactionDao(db: AppDatabase): ImportedTransactionDao = db.importedTransactionDao()
    @Provides fun provideExternalAuthDao(db: AppDatabase): ExternalAuthDao = db.externalAuthDao()
    @Provides fun provideCloudSettingDao(db: AppDatabase): CloudSettingDao = db.cloudSettingDao()
}
