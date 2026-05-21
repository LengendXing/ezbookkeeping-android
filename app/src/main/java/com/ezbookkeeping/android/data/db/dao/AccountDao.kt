package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM ezbk_accounts WHERE userId = :userId ORDER BY `order`")
    fun getByUserId(userId: Int): Flow<List<AccountEntity>>

    @Query("SELECT * FROM ezbk_accounts WHERE id = :id")
    fun getById(id: Int): Flow<AccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(accounts: List<AccountEntity>)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("DELETE FROM ezbk_accounts WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)
}
