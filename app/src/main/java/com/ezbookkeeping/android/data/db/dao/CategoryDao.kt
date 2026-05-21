package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM ezbk_categories WHERE userId = :userId AND type = :type ORDER BY `order`")
    fun getByType(userId: Int, type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM ezbk_categories WHERE userId = :userId ORDER BY `order`")
    fun getByUserId(userId: Int): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM ezbk_categories WHERE id = :id")
    fun getById(id: Int): Flow<CategoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<CategoryEntity>)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("DELETE FROM ezbk_categories WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)
}
