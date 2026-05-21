package com.ezbookkeeping.android.data.repository

import com.ezbookkeeping.android.data.db.dao.UserDao
import com.ezbookkeeping.android.data.db.entity.UserEntity
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import com.ezbookkeeping.android.data.remote.dto.LoginRequest
import com.ezbookkeeping.android.data.remote.dto.SignupRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val api: EZBookkeepingApi
) {
    fun getCurrentUser(id: Int): Flow<UserEntity?> = userDao.getById(id)
    fun getUserByUsername(username: String): Flow<UserEntity?> = userDao.getByUsername(username)
    suspend fun upsertUser(user: UserEntity) = userDao.upsert(user)

    suspend fun login(username: String, password: String, twoFactorCode: String? = null) =
        api.login(LoginRequest(username, password, twoFactorCode))

    suspend fun signup(username: String, password: String, email: String, nickname: String?) =
        api.signup(SignupRequest(username, password, email, nickname))

    suspend fun logout() = api.logout()
}
