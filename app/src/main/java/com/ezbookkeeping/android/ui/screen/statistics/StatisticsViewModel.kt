package com.ezbookkeeping.android.ui.screen.statistics

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.data.db.entity.CategoryEntity
import com.ezbookkeeping.android.data.repository.TransactionRepository
import com.ezbookkeeping.android.data.repository.CategoryRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import com.ezbookkeeping.android.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ChartType { PIE, BAR, RADAR, TREND }
enum class DataDataType { EXPENSE, INCOME, BOTH }
enum class DateAggregation { DAY, WEEK, MONTH, YEAR }
enum class SortMethod { AMOUNT_ASC, AMOUNT_DESC }

data class CategoryStat(val categoryId: Int?, val name: String, val color: String, val amount: Double, val percentage: Float)

@Stable
data class StatisticsUiState(
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalTransfer: Double = 0.0,
    val expenseByCategory: List<CategoryStat> = emptyList(),
    val incomeByCategory: List<CategoryStat> = emptyList(),
    val isLoading: Boolean = false,
    val dateRange: DateRange = DateRange.THIS_MONTH,
    val categories: List<CategoryEntity> = emptyList(),
    val chartType: ChartType = ChartType.PIE,
    val dataType: DataDataType = DataDataType.EXPENSE,
    val aggregation: DateAggregation = DateAggregation.MONTH,
    val sortMethod: SortMethod = SortMethod.AMOUNT_DESC
)

enum class DateRange(val label: String) {
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    THIS_YEAR("This Year"),
    ALL("All Time")
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadStatistics()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepo.getByUserId(authState.userId).distinctUntilChanged().collect { list -> _uiState.update { it.copy(categories = list) } }
        }
    }

    fun setDateRange(range: DateRange) {
        _uiState.update { it.copy(dateRange = range) }
        loadStatistics()
    }

    fun setChartType(type: ChartType) { _uiState.update { it.copy(chartType = type) } }
    fun setDataType(type: DataDataType) { _uiState.update { it.copy(dataType = type) } }
    fun setAggregation(agg: DateAggregation) { _uiState.update { it.copy(aggregation = agg) } }
    fun setSortMethod(method: SortMethod) { _uiState.update { it.copy(sortMethod = method) } }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val (start, end) = dateRangeToPair(_uiState.value.dateRange)
            transactionRepo.getByDateRange(authState.userId, start, end)
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .distinctUntilChanged().collect { list ->
                    val cats = _uiState.value.categories
                    val expense = list.filter { it.type == TransactionType.EXPENSE }
                    val income = list.filter { it.type == TransactionType.INCOME }
                    val transfer = list.filter { it.type == TransactionType.TRANSFER }
                    val totalExp = expense.sumOf { it.sourceAmount }
                    val totalInc = income.sumOf { it.sourceAmount }
                    val totalTrf = transfer.sumOf { it.sourceAmount }

                    val expByCat = expense.groupBy { it.categoryId }.map { (catId, txs) ->
                        val amt = txs.sumOf { it.sourceAmount }
                        val cat = cats.find { it.id == catId }
                        CategoryStat(catId, cat?.name ?: "Category $catId", cat?.color ?: "#6200EE", amt, if (totalExp > 0) (amt / totalExp * 100).toFloat() else 0f)
                    }.sortedByDescending { it.amount }

                    val incByCat = income.groupBy { it.categoryId }.map { (catId, txs) ->
                        val amt = txs.sumOf { it.sourceAmount }
                        val cat = cats.find { it.id == catId }
                        CategoryStat(catId, cat?.name ?: "Category $catId", cat?.color ?: "#6200EE", amt, if (totalInc > 0) (amt / totalInc * 100).toFloat() else 0f)
                    }.sortedByDescending { it.amount }

                    _uiState.update { it.copy(totalExpense = totalExp, totalIncome = totalInc, totalTransfer = totalTrf, expenseByCategory = expByCat, incomeByCategory = incByCat, isLoading = false) }
                }
        }
    }

    private fun dateRangeToPair(range: DateRange): Pair<String, String> {
        return when (range) {
            DateRange.THIS_MONTH -> DateUtil.monthStart() to DateUtil.monthEnd()
            DateRange.LAST_MONTH -> DateUtil.lastMonthStart() to DateUtil.lastMonthEnd()
            DateRange.THIS_YEAR -> DateUtil.yearStart() to DateUtil.yearEnd()
            DateRange.ALL -> "1970-01-01" to "2099-12-31"
        }
    }
}
