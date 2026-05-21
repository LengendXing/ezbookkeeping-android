package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.ImportedTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportedTransactionDao {
    @Query("SELECT * FROM ezbk_imported_transactions WHERE userId = :userId")
    fun getByUserId(userId: Int): Flow<List<ImportedTransactionEntity>>

    @Query("SELECT * FROM ezbk_imported_transactions WHERE id = :id")
    fun getById(id: Int): Flow<ImportedTransactionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ImportedTransactionEntity)

    @Delete
    suspend fun delete(item: ImportedTransactionEntity)

    @Query("DELETE FROM ezbk_imported_transactions WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)
}
