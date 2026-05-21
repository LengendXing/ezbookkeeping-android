package com.ezbookkeeping.android.data.repository

import com.ezbookkeeping.android.data.db.dao.TagDao
import com.ezbookkeeping.android.data.db.entity.TagEntity
import com.ezbookkeeping.android.data.db.entity.TagGroupEntity
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val tagDao: TagDao,
    private val api: EZBookkeepingApi
) {
    fun getTags(userId: Int): Flow<List<TagEntity>> = tagDao.getTagsByUserId(userId)
    fun getTagById(id: Int): Flow<TagEntity?> = tagDao.getTagById(id)
    fun getGroups(userId: Int): Flow<List<TagGroupEntity>> = tagDao.getGroupsByUserId(userId)
    fun getGroupById(id: Int): Flow<TagGroupEntity?> = tagDao.getGroupById(id)
    suspend fun upsertTag(tag: TagEntity) = tagDao.upsertTag(tag)
    suspend fun upsertAllTags(tags: List<TagEntity>) = tagDao.upsertAllTags(tags)
    suspend fun upsertGroup(group: TagGroupEntity) = tagDao.upsertGroup(group)
    suspend fun upsertAllGroups(groups: List<TagGroupEntity>) = tagDao.upsertAllGroups(groups)
    suspend fun deleteTag(tag: TagEntity) = tagDao.deleteTag(tag)
    suspend fun deleteGroup(group: TagGroupEntity) = tagDao.deleteGroup(group)

    suspend fun fetchRemoteTags() = api.getTags()
}
