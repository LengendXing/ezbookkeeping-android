package com.ezbookkeeping.android.data.repository

import com.ezbookkeeping.android.data.db.dao.TemplateDao
import com.ezbookkeeping.android.data.db.entity.TemplateEntity
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TemplateRepository @Inject constructor(
    private val templateDao: TemplateDao,
    private val api: EZBookkeepingApi
) {
    fun getByUserId(userId: Int): Flow<List<TemplateEntity>> = templateDao.getByUserId(userId)
    fun getById(id: Int): Flow<TemplateEntity?> = templateDao.getById(id)
    suspend fun upsert(template: TemplateEntity) = templateDao.upsert(template)
    suspend fun upsertAll(templates: List<TemplateEntity>) = templateDao.upsertAll(templates)
    suspend fun delete(template: TemplateEntity) = templateDao.delete(template)

    suspend fun fetchRemoteTemplates() = api.getTemplates()
}
