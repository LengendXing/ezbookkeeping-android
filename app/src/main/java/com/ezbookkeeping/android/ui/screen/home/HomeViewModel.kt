package com.ezbookkeeping.android.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.local.UserPreferences
import com.ezbookkeeping.android.data.repository.TransactionRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import com.ezbookkeeping.android.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(val transactions: List<TransactionEntity> = emptyList(), val totalExpense: Double = 0.0, val totalIncome: Double = 0.0, val isLoading: Boolean = false)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val authState: AuthState,
    private val prefs: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadTransactions() }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepo.getByDateRange(authState.userId, DateUtil.monthStart(), DateUtil.monthEnd())
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { list ->
                    val expense = list.filter { it.type == com.ezbookkeeping.android.data.db.entity.TransactionType.EXPENSE }.sumOf { it.sourceAmount }
                    val income = list.filter { it.type == com.ezbookkeeping.android.data.db.entity.TransactionType.INCOME }.sumOf { it.sourceAmount }
                    _uiState.update { it.copy(transactions = list, totalExpense = expense, totalIncome = income, isLoading = false) }
                }
        }
    }
}
