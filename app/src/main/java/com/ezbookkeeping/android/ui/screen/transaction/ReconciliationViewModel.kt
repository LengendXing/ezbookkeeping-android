package com.ezbookkeeping.android.ui.screen.transaction

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ReconciliationRow(
    val date: String,
    val description: String,
    val statementAmount: Double,
    val bookAmount: Double,
    val difference: Double,
    val isMatched: Boolean
)

data class ReconciliationUiState(
    val isLoading: Boolean = false,
    val statementBalance: Double = 0.0,
    val bookBalance: Double = 0.0,
    val difference: Double = 0.0,
    val reconciliationDate: String = "",
    val rows: List<ReconciliationRow> = emptyList(),
    val isReconciling: Boolean = false,
    val result: String? = null
)

@HiltViewModel
class ReconciliationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ReconciliationUiState())
    val uiState: StateFlow<ReconciliationUiState> = _uiState.asStateFlow()

    fun setStatementBalance(amount: Double) {
        val diff = amount - _uiState.value.bookBalance
        _uiState.update { it.copy(statementBalance = amount, difference = diff) }
    }

    fun loadSampleData() {
        _uiState.update { it.copy(isLoading = true) }
        val rows = listOf(
            ReconciliationRow("2025-01-01", "Opening Balance", 10000.0, 10000.0, 0.0, true),
            ReconciliationRow("2025-01-05", "Salary Deposit", 5000.0, 5000.0, 0.0, true),
            ReconciliationRow("2025-01-10", "Rent Payment", -2000.0, -2100.0, -100.0, false),
            ReconciliationRow("2025-01-15", "Grocery", -150.0, -150.0, 0.0, true),
            ReconciliationRow("2025-01-20", "Transfer In", 800.0, 800.0, 0.0, true),
            ReconciliationRow("2025-01-25", "Utility Bill", -200.0, -200.0, 0.0, true)
        )
        val stmtBal = 10000.0 + 5000.0 + 800.0
        val bookBal = 10000.0 + 5000.0 - 2100.0 - 150.0 + 800.0 - 200.0
        _uiState.update { it.copy(isLoading = false, rows = rows, statementBalance = stmtBal, bookBalance = bookBal, difference = stmtBal - bookBal, reconciliationDate = "2025-01-31") }
    }

    fun reconcile() {
        _uiState.update { it.copy(isReconciling = true) }
        val unmatched = _uiState.value.rows.count { !it.isMatched }
        val msg = if (unmatched == 0) "All items matched. Account is reconciled." else "$unmatched item(s) have discrepancies that need review."
        _uiState.update { it.copy(isReconciling = false, result = msg) }
    }
}
