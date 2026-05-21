package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM ezbk_transactions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, time DESC")
    fun getByDateRange(userId: Int, startDate: String, endDate: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM ezbk_transactions WHERE id = :id")
    fun getById(id: Int): Flow<TransactionEntity?>

    @Query("SELECT * FROM ezbk_transactions WHERE userId = :userId AND categoryId = :categoryId")
    fun getByCategoryId(userId: Int, categoryId: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM ezbk_transactions WHERE userId = :userId AND sourceAccountId = :accountId OR destinationAccountId = :accountId")
    fun getByAccountId(userId: Int, accountId: Int): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(transactions: List<TransactionEntity>)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM ezbk_transactions WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)
}
