package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.AccountEntity
import com.ezbookkeeping.android.data.repository.AccountRepository
import com.ezbookkeeping.android.data.repository.TransactionRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import com.ezbookkeeping.android.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReconciliationRow(
    val date: String,
    val description: String,
    val statementAmount: Double,
    val bookAmount: Double,
    val difference: Double,
    val isMatched: Boolean
)

@Stable
data class ReconciliationUiState(
    val isLoading: Boolean = false,
    val statementBalance: Double = 0.0,
    val bookBalance: Double = 0.0,
    val difference: Double = 0.0,
    val openingBalance: Double = -1.0,
    val closingBalance: Double = -1.0,
    val reconciliationDate: String = "",
    val rows: List<ReconciliationRow> = emptyList(),
    val isReconciling: Boolean = false,
    val result: String? = null,
    val accounts: List<AccountEntity> = emptyList(),
    val selectedAccountId: Int? = null,
    val selectedAccountName: String? = null
)

@HiltViewModel
class ReconciliationViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
    private val transactionRepo: TransactionRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReconciliationUiState())
    val uiState: StateFlow<ReconciliationUiState> = _uiState.asStateFlow()

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            accountRepo.getAccounts(authState.userId)
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .distinctUntilChanged().collect { list -> _uiState.update { it.copy(accounts = list, isLoading = false) } }
        }
    }

    fun selectAccount(accountId: Int) {
        val acct = _uiState.value.accounts.find { it.id == accountId }
        _uiState.update { it.copy(selectedAccountId = accountId, selectedAccountName = acct?.name, openingBalance = -1.0, closingBalance = -1.0, rows = emptyList(), result = null) }
        loadAccountTransactions(accountId)
    }

    private fun loadAccountTransactions(accountId: Int) {
        viewModelScope.launch {
            transactionRepo.getByDateRange(authState.userId, "1970-01-01", "2099-12-31")
                .first()
                .filter { it.sourceAccountId == accountId || it.destinationAccountId == accountId }
                .let { txs ->
                    val rows = txs.map { tx ->
                        val amount = if (tx.sourceAccountId == accountId) -tx.sourceAmount else (tx.destinationAmount ?: 0.0)
                        ReconciliationRow(tx.date, tx.comment ?: "Transaction", amount, amount, 0.0, true)
                    }
                    val bookBal = rows.sumOf { it.bookAmount }
                    _uiState.update { it.copy(rows = rows, bookBalance = bookBal, difference = if (_uiState.value.closingBalance >= 0) _uiState.value.closingBalance - bookBal else 0.0) }
                }
        }
    }

    fun setOpeningBalance(amountStr: String) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(openingBalance = amount) }
    }

    fun setClosingBalance(amountStr: String) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(closingBalance = amount, statementBalance = amount, difference = amount - _uiState.value.bookBalance) }
    }

    fun createAdjustBalance(navController: androidx.navigation.NavController) {
        val accountId = _uiState.value.selectedAccountId ?: return
        navController.navigate(Routes.TRANSACTION_EDIT)
    }

    fun reconcile() {
        _uiState.update { it.copy(isReconciling = true) }
        val unmatched = _uiState.value.rows.count { !it.isMatched }
        val msg = if (unmatched == 0 && _uiState.value.difference == 0.0) "All items matched. Account is reconciled." else "${unmatched} item(s) have discrepancies. Difference: ${_uiState.value.difference}"
        _uiState.update { it.copy(isReconciling = false, result = msg) }
    }
}
