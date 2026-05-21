package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.TokenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Query("SELECT * FROM ezbk_tokens WHERE userId = :userId AND type = :type")
    fun getByUserIdAndType(userId: Int, type: String): Flow<List<TokenEntity>>

    @Query("SELECT * FROM ezbk_tokens WHERE token = :token")
    fun getByToken(token: String): Flow<TokenEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(token: TokenEntity)

    @Delete
    suspend fun delete(token: TokenEntity)

    @Query("DELETE FROM ezbk_tokens WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)
}
