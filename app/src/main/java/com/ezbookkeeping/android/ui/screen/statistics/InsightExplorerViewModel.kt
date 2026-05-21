package com.ezbookkeeping.android.ui.screen.statistics

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TrendPoint(val month: String, val expense: Double, val income: Double)

data class InsightData(
    val title: String,
    val value: String,
    val trend: String,
    val isPositive: Boolean
)

data class InsightExplorerUiState(
    val isLoading: Boolean = false,
    val selectedPeriod: InsightPeriod = InsightPeriod.MONTHLY,
    val insights: List<InsightData> = emptyList(),
    val trendData: List<TrendPoint> = emptyList(),
    val topExpenseCategories: List<CategoryInsight> = emptyList(),
    val savingsRate: Float = 0f
)

enum class InsightPeriod(val label: String) { WEEKLY("Weekly"), MONTHLY("Monthly"), YEARLY("Yearly") }

data class CategoryInsight(val name: String, val amount: Double, val percentage: Float, val colorHex: String)

@HiltViewModel
class InsightExplorerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(InsightExplorerUiState())
    val uiState: StateFlow<InsightExplorerUiState> = _uiState.asStateFlow()

    init { loadData(InsightPeriod.MONTHLY) }

    fun setPeriod(period: InsightPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadData(period)
    }

    private fun loadData(period: InsightPeriod) {
        _uiState.update { it.copy(isLoading = true) }
        val insights = listOf(
            InsightData("Total Expenses", "2,350.00", "-8.2% vs last period", false),
            InsightData("Total Income", "5,000.00", "+3.1% vs last period", true),
            InsightData("Net Savings", "2,650.00", "+12.5% vs last period", true),
            InsightData("Avg Daily Expense", "78.33", "-5.1% vs last period", true)
        )
        val trends = listOf(
            TrendPoint("Sep", 2800.0, 4800.0),
            TrendPoint("Oct", 2500.0, 5000.0),
            TrendPoint("Nov", 3100.0, 5200.0),
            TrendPoint("Dec", 2900.0, 5500.0),
            TrendPoint("Jan", 2350.0, 5000.0)
        )
        val topCats = listOf(
            CategoryInsight("Food & Dining", 850.0, 36.2f, "#FF6B6B"),
            CategoryInsight("Housing", 700.0, 29.8f, "#4ECDC4"),
            CategoryInsight("Transport", 350.0, 14.9f, "#45B7D1"),
            CategoryInsight("Shopping", 280.0, 11.9f, "#96CEB4"),
            CategoryInsight("Others", 170.0, 7.2f, "#FFEAA7")
        )
        _uiState.update { it.copy(isLoading = false, insights = insights, trendData = trends, topExpenseCategories = topCats, savingsRate = 53.0f) }
    }
}
