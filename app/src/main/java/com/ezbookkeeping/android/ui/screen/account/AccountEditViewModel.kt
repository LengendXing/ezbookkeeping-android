package com.ezbookkeeping.android.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.AccountEntity
import com.ezbookkeeping.android.data.db.entity.AccountType
import com.ezbookkeeping.android.data.repository.AccountRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountEditUiState(
    val name: String = "", val type: AccountType = AccountType.ASSET, val currency: String = "CNY",
    val icon: String = "wallet", val color: String = "#1B6B4D",
    val initialBalance: String = "0", val balance: String = "0", val creditLimit: String = "0",
    val isEdit: Boolean = false, val isLoading: Boolean = false, val error: String? = null, val saveSuccess: Boolean = false,
    val accountId: Int = 0
)

@HiltViewModel
class AccountEditViewModel @Inject constructor(private val accountRepo: AccountRepository, private val authState: AuthState) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountEditUiState())
    val uiState: StateFlow<AccountEditUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String) { _uiState.update { it.copy(name = v, error = null) } }
    fun onTypeChange(v: AccountType) { _uiState.update { it.copy(type = v) } }
    fun onCurrencyChange(v: String) { _uiState.update { it.copy(currency = v.uppercase()) } }
    fun onColorChange(v: String) { _uiState.update { it.copy(color = v) } }
    fun onInitialBalanceChange(v: String) { _uiState.update { it.copy(initialBalance = v) } }
    fun onBalanceChange(v: String) { _uiState.update { it.copy(balance = v) } }
    fun onCreditLimitChange(v: String) { _uiState.update { it.copy(creditLimit = v) } }

    fun loadAccount(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEdit = true, accountId = id) }
            accountRepo.getAccountById(id).first()?.let { a ->
                _uiState.update { it.copy(name = a.name, type = a.type, currency = a.currency, icon = a.icon, color = a.color, initialBalance = a.initialBalance.toString(), balance = a.balance.toString(), creditLimit = a.creditLimit.toString()) }
            }
        }
    }

    fun save() {
        val s = _uiState.value
        if (s.name.isBlank()) { _uiState.update { it.copy(error = "Name required") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val initBal = s.initialBalance.toDoubleOrNull() ?: 0.0
                val bal = if (s.isEdit) s.balance.toDoubleOrNull() ?: initBal else initBal
                val creditLim = s.creditLimit.toDoubleOrNull() ?: 0.0
                accountRepo.upsertAccount(AccountEntity(
                    id = if (s.isEdit) s.accountId else System.currentTimeMillis().hashCode(),
                    userId = authState.userId, type = s.type, name = s.name, icon = s.icon, color = s.color,
                    currency = s.currency, initialBalance = initBal, balance = bal, creditLimit = creditLim
                ))
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) { _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
