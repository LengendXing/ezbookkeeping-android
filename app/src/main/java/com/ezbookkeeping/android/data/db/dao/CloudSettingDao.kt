package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.CloudSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudSettingDao {
    @Query("SELECT * FROM ezbk_cloud_settings WHERE userId = :userId AND `key` = :key")
    fun getByKey(userId: Int, key: String): Flow<CloudSettingEntity?>

    @Query("SELECT * FROM ezbk_cloud_settings WHERE userId = :userId")
    fun getByUserId(userId: Int): Flow<List<CloudSettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: CloudSettingEntity)

    @Delete
    suspend fun delete(setting: CloudSettingEntity)
}
