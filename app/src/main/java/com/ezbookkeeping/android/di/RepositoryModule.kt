package com.ezbookkeeping.android.di

import com.ezbookkeeping.android.data.db.dao.*
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import com.ezbookkeeping.android.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides @Singleton
    fun provideUserRepository(userDao: UserDao, api: EZBookkeepingApi): UserRepository =
        UserRepository(userDao, api)

    @Provides @Singleton
    fun provideAccountRepository(accountDao: AccountDao, api: EZBookkeepingApi): AccountRepository =
        AccountRepository(accountDao, api)

    @Provides @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao, api: EZBookkeepingApi): TransactionRepository =
        TransactionRepository(transactionDao, api)

    @Provides @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao, api: EZBookkeepingApi): CategoryRepository =
        CategoryRepository(categoryDao, api)

    @Provides @Singleton
    fun provideTagRepository(tagDao: TagDao, api: EZBookkeepingApi): TagRepository =
        TagRepository(tagDao, api)

    @Provides @Singleton
    fun provideTemplateRepository(templateDao: TemplateDao, api: EZBookkeepingApi): TemplateRepository =
        TemplateRepository(templateDao, api)

    @Provides @Singleton
    fun provideExchangeRateRepository(exchangeRateDao: ExchangeRateDao, api: EZBookkeepingApi): ExchangeRateRepository =
        ExchangeRateRepository(exchangeRateDao, api)
}
