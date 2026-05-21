package com.ezbookkeeping.android.data.repository

import com.ezbookkeeping.android.data.db.dao.TransactionDao
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import com.ezbookkeeping.android.data.remote.dto.CreateTransactionRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val api: EZBookkeepingApi
) {
    fun getByDateRange(userId: Int, start: String, end: String): Flow<List<TransactionEntity>> =
        transactionDao.getByDateRange(userId, start, end)

    fun getById(id: Int): Flow<TransactionEntity?> = transactionDao.getById(id)
    suspend fun upsert(transaction: TransactionEntity) = transactionDao.upsert(transaction)
    suspend fun upsertAll(transactions: List<TransactionEntity>) = transactionDao.upsertAll(transactions)
    suspend fun delete(transaction: TransactionEntity) = transactionDao.delete(transaction)

    suspend fun fetchRemoteTransactions(start: String, end: String, page: Int = 0) =
        api.getTransactions(start, end, page)

    suspend fun createRemoteTransaction(request: CreateTransactionRequest) = api.createTransaction(request)
    suspend fun updateRemoteTransaction(id: Int, request: CreateTransactionRequest) = api.updateTransaction(id, request)
    suspend fun deleteRemoteTransaction(id: Int) = api.deleteTransaction(id)
}
