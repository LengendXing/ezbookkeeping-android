package com.ezbookkeeping.android.data.repository

import com.ezbookkeeping.android.data.db.dao.AccountDao
import com.ezbookkeeping.android.data.db.entity.AccountEntity
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import com.ezbookkeeping.android.data.remote.dto.CreateAccountRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val api: EZBookkeepingApi
) {
    fun getAccounts(userId: Int): Flow<List<AccountEntity>> = accountDao.getByUserId(userId)
    fun getAccountById(id: Int): Flow<AccountEntity?> = accountDao.getById(id)
    suspend fun upsertAccount(account: AccountEntity) = accountDao.upsert(account)
    suspend fun upsertAll(accounts: List<AccountEntity>) = accountDao.upsertAll(accounts)
    suspend fun deleteAccount(account: AccountEntity) = accountDao.delete(account)

    suspend fun fetchRemoteAccounts() = api.getAccounts()
    suspend fun createRemoteAccount(request: CreateAccountRequest) = api.createAccount(request)
}
