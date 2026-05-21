package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.ExternalAuthEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExternalAuthDao {
    @Query("SELECT * FROM ezbk_external_auths WHERE userId = :userId")
    fun getByUserId(userId: Int): Flow<List<ExternalAuthEntity>>

    @Query("SELECT * FROM ezbk_external_auths WHERE provider = :provider AND providerUserId = :providerUserId")
    fun getByProvider(provider: String, providerUserId: String): Flow<ExternalAuthEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(auth: ExternalAuthEntity)

    @Delete
    suspend fun delete(auth: ExternalAuthEntity)
}
