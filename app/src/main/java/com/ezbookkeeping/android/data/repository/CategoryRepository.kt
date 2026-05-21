package com.ezbookkeeping.android.data.repository

import com.ezbookkeeping.android.data.db.dao.CategoryDao
import com.ezbookkeeping.android.data.db.entity.CategoryEntity
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import com.ezbookkeeping.android.data.remote.dto.CreateCategoryRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val api: EZBookkeepingApi
) {
    fun getByType(userId: Int, type: String): Flow<List<CategoryEntity>> = categoryDao.getByType(userId, type)
    fun getByUserId(userId: Int): Flow<List<CategoryEntity>> = categoryDao.getByUserId(userId)
    fun getById(id: Int): Flow<CategoryEntity?> = categoryDao.getById(id)
    suspend fun upsert(category: CategoryEntity) = categoryDao.upsert(category)
    suspend fun upsertAll(categories: List<CategoryEntity>) = categoryDao.upsertAll(categories)
    suspend fun delete(category: CategoryEntity) = categoryDao.delete(category)

    suspend fun fetchRemoteCategories() = api.getCategories()
    suspend fun createRemoteCategory(request: CreateCategoryRequest) = api.createCategory(request)
}
