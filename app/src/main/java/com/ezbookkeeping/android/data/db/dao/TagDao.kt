package com.ezbookkeeping.android.data.db.dao

import androidx.room.*
import com.ezbookkeeping.android.data.db.entity.TagEntity
import com.ezbookkeeping.android.data.db.entity.TagGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM ezbk_tags WHERE userId = :userId")
    fun getTagsByUserId(userId: Int): Flow<List<TagEntity>>

    @Query("SELECT * FROM ezbk_tags WHERE id = :id")
    fun getTagById(id: Int): Flow<TagEntity?>

    @Query("SELECT * FROM ezbk_tag_groups WHERE userId = :userId ORDER BY `order`")
    fun getGroupsByUserId(userId: Int): Flow<List<TagGroupEntity>>

    @Query("SELECT * FROM ezbk_tag_groups WHERE id = :id")
    fun getGroupById(id: Int): Flow<TagGroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllTags(tags: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGroup(group: TagGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllGroups(groups: List<TagGroupEntity>)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Delete
    suspend fun deleteGroup(group: TagGroupEntity)
}
