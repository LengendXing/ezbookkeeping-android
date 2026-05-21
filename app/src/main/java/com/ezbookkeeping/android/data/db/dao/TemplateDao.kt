package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.TemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM ezbk_templates WHERE userId = :userId ORDER BY `order`")
    fun getByUserId(userId: Int): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM ezbk_templates WHERE id = :id")
    fun getById(id: Int): Flow<TemplateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(template: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(templates: List<TemplateEntity>)

    @Delete
    suspend fun delete(template: TemplateEntity)

    @Query("DELETE FROM ezbk_templates WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)
}
