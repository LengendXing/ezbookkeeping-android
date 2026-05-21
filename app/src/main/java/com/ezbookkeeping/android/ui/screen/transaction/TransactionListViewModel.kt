package com.ezbookkeeping.android.ui.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.data.repository.TransactionRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import com.ezbookkeeping.android.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _typeFilter = MutableStateFlow<TransactionType?>(null)
    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())

    init {
        viewModelScope.launch {
            combine(_searchQuery, _typeFilter, _transactions) { query, type, txs ->
                var filtered = txs
                if (type != null) filtered = filtered.filter { it.type == type }
                if (query.isNotBlank()) filtered = filtered.filter { it.comment?.contains(query, ignoreCase = true) == true }
                val groups = filtered.groupBy { it.date.substring(0, 7) }.map { (ym, list) ->
                    TransactionMonthGroup(
                        yearMonth = ym,
                        transactions = list.sortedByDescending { it.date + (it.time ?: "") },
                        totalExpense = list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.sourceAmount },
                        totalIncome = list.filter { it.type == TransactionType.INCOME }.sumOf { it.sourceAmount }
                    )
                }.sortedByDescending { it.yearMonth }
                _uiState.update { it.copy(searchQuery = query, typeFilter = type, monthGroups = groups) }
            }.collect()
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepo.getByDateRange(authState.userId, DateUtil.yearStart(), DateUtil.yearEnd())
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { list ->
                    _transactions.value = list
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun onTypeFilterChange(type: TransactionType?) { _typeFilter.value = type }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            transactionRepo.delete(tx)
            _transactions.value = _transactions.value.filter { it.id != tx.id }
        }
    }
}
