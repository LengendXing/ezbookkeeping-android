package com.ezbookkeeping.android.ui.screen.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.TemplateEntity
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.data.local.UserPreferences
import com.ezbookkeeping.android.data.repository.TemplateRepository
import com.ezbookkeeping.android.data.repository.TransactionRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import com.ezbookkeeping.android.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@Stable
data class HomeUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val templates: List<TemplateEntity> = emptyList(),
    val monthExpense: Double = 0.0,
    val monthIncome: Double = 0.0,
    val todayExpense: Double = 0.0,
    val todayIncome: Double = 0.0,
    val thisWeekExpense: Double = 0.0,
    val thisWeekIncome: Double = 0.0,
    val thisYearExpense: Double = 0.0,
    val thisYearIncome: Double = 0.0,
    val showAmount: Boolean = true,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val monthLabel: String = "",
    val todayDate: String = "",
    val thisWeekRange: String = "",
    val thisMonthRange: String = "",
    val thisYearLabel: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val templateRepo: TemplateRepository,
    private val authState: AuthState,
    private val prefs: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("MM/dd", Locale.getDefault())
    private val monthFormatter = SimpleDateFormat("yyyy/MM", Locale.getDefault())
    private val yearFormatter = SimpleDateFormat("yyyy", Locale.getDefault())
    private val todayFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    init {
        loadDateLabels()
        loadTransactions()
        loadTemplates()
    }

    private fun loadDateLabels() {
        val now = Calendar.getInstance()
        val today = todayFormatter.format(now.time)
        val month = monthFormatter.format(now.time)
        val year = yearFormatter.format(now.time)

        val weekStart = (now.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        val weekEnd = (now.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek + 6)
        }
        val weekRange = "${dateFormatter.format(weekStart.time)} - ${dateFormatter.format(weekEnd.time)}"

        val monthStart = (now.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
        val monthEnd = (now.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        val monthRange = "${dateFormatter.format(monthStart.time)} - ${dateFormatter.format(monthEnd.time)}"

        _uiState.update {
            it.copy(
                monthLabel = month,
                todayDate = today,
                thisWeekRange = weekRange,
                thisMonthRange = monthRange,
                thisYearLabel = year
            )
        }
    }

    fun toggleShowAmount() {
        _uiState.update { it.copy(showAmount = !it.showAmount) }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadTransactionsInternal()
            loadDateLabels()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            templateRepo.getByUserId(authState.userId)
                .catch { }
                .distinctUntilChanged().collect { list -> _uiState.update { it.copy(templates = list) } }
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadTransactionsInternal()
        }
    }

    private suspend fun loadTransactionsInternal() {
        val now = Calendar.getInstance()
        val userId = authState.userId

        transactionRepo.getByDateRange(userId, DateUtil.monthStart(), DateUtil.monthEnd())
            .catch { _uiState.update { it.copy(isLoading = false) } }
            .distinctUntilChanged().collect { monthList ->
                val monthExp = monthList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.sourceAmount }
                val monthInc = monthList.filter { it.type == TransactionType.INCOME }.sumOf { it.sourceAmount }

                transactionRepo.getByDateRange(userId, DateUtil.dayStart(), DateUtil.dayEnd())
                    .catch { }
                    .distinctUntilChanged().collect { todayList ->
                        val todayExp = todayList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.sourceAmount }
                        val todayInc = todayList.filter { it.type == TransactionType.INCOME }.sumOf { it.sourceAmount }

                        transactionRepo.getByDateRange(userId, DateUtil.weekStart(), DateUtil.weekEnd())
                            .catch { }
                            .distinctUntilChanged().collect { weekList ->
                                val weekExp = weekList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.sourceAmount }
                                val weekInc = weekList.filter { it.type == TransactionType.INCOME }.sumOf { it.sourceAmount }

                                transactionRepo.getByDateRange(userId, DateUtil.yearStart(), DateUtil.yearEnd())
                                    .catch { }
                                    .distinctUntilChanged().collect { yearList ->
                                        val yearExp = yearList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.sourceAmount }
                                        val yearInc = yearList.filter { it.type == TransactionType.INCOME }.sumOf { it.sourceAmount }

                                        _uiState.update {
                                            it.copy(
                                                transactions = monthList,
                                                monthExpense = monthExp,
                                                monthIncome = monthInc,
                                                todayExpense = todayExp,
                                                todayIncome = todayInc,
                                                thisWeekExpense = weekExp,
                                                thisWeekIncome = weekInc,
                                                thisYearExpense = yearExp,
                                                thisYearIncome = yearInc,
                                                isLoading = false
                                            )
                                        }
                                    }
                            }
                    }
            }
    }
}
