package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM ezbk_exchange_rates WHERE currency = :currency")
    fun getByCurrency(currency: String): Flow<ExchangeRateEntity?>

    @Query("SELECT * FROM ezbk_exchange_rates")
    fun getAll(): Flow<List<ExchangeRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rate: ExchangeRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rates: List<ExchangeRateEntity>)

    @Query("DELETE FROM ezbk_exchange_rates")
    suspend fun deleteAll()
}
