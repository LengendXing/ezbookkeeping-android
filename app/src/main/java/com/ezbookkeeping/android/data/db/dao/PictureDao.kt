package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.PictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {
    @Query("SELECT * FROM ezbk_pictures WHERE transactionId = :transactionId")
    fun getByTransactionId(transactionId: Int): Flow<List<PictureEntity>>

    @Query("SELECT * FROM ezbk_pictures WHERE id = :id")
    fun getById(id: Int): Flow<PictureEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(picture: PictureEntity)

    @Delete
    suspend fun delete(picture: PictureEntity)

    @Query("DELETE FROM ezbk_pictures WHERE transactionId = :transactionId")
    suspend fun deleteByTransactionId(transactionId: Int)
}
