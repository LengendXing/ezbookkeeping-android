package com.ezbookkeeping.android.ui.screen.statistics

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

data class CategoryStat(val categoryId: Int?, val name: String, val amount: Double, val percentage: Float)
data class StatisticsUiState(val totalExpense: Double = 0.0, val totalIncome: Double = 0.0, val expenseByCategory: List<CategoryStat> = emptyList(), val incomeByCategory: List<CategoryStat> = emptyList(), val isLoading: Boolean = false)

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val transactionRepo: TransactionRepository, private val authState: AuthState) : ViewModel() {
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init { loadStatistics() }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepo.getByDateRange(authState.userId, DateUtil.monthStart(), DateUtil.monthEnd()).catch { _uiState.update { it.copy(isLoading = false) } }.collect { list ->
                val expense = list.filter { it.type == TransactionType.EXPENSE }
                val income = list.filter { it.type == TransactionType.INCOME }
                val totalExp = expense.sumOf { it.sourceAmount }
                val totalInc = income.sumOf { it.sourceAmount }
                val expByCat = expense.groupBy { it.categoryId }.map { (catId, txs) ->
                    val amt = txs.sumOf { it.sourceAmount }
                    CategoryStat(catId, "Category $catId", amt, if (totalExp > 0) (amt / totalExp * 100).toFloat() else 0f)
                }.sortedByDescending { it.amount }
                val incByCat = income.groupBy { it.categoryId }.map { (catId, txs) ->
                    val amt = txs.sumOf { it.sourceAmount }
                    CategoryStat(catId, "Category $catId", amt, if (totalInc > 0) (amt / totalInc * 100).toFloat() else 0f)
                }.sortedByDescending { it.amount }
                _uiState.update { it.copy(totalExpense = totalExp, totalIncome = totalInc, expenseByCategory = expByCat, incomeByCategory = incByCat, isLoading = false) }
            }
        }
    }
}
