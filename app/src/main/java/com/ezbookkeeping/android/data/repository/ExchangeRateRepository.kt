package com.ezbookkeeping.android.data.repository

import com.ezbookkeeping.android.data.db.dao.ExchangeRateDao
import com.ezbookkeeping.android.data.db.entity.ExchangeRateEntity
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExchangeRateRepository @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
    private val api: EZBookkeepingApi
) {
    fun getByCurrency(currency: String): Flow<ExchangeRateEntity?> = exchangeRateDao.getByCurrency(currency)
    fun getAll(): Flow<List<ExchangeRateEntity>> = exchangeRateDao.getAll()
    suspend fun upsert(rate: ExchangeRateEntity) = exchangeRateDao.upsert(rate)
    suspend fun upsertAll(rates: List<ExchangeRateEntity>) = exchangeRateDao.upsertAll(rates)

    suspend fun fetchRemoteRates() = api.getExchangeRates()
}
