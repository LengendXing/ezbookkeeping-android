package com.ezbookkeeping.android.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.AccountEntity
import com.ezbookkeeping.android.data.repository.AccountRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val accountRepo: AccountRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountListUiState())
    val uiState: StateFlow<AccountListUiState> = _uiState.asStateFlow()

    init { loadAccounts() }

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            accountRepo.getAccounts(authState.userId)
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { list -> _uiState.update { it.copy(accounts = list, isLoading = false) } }
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountRepo.deleteAccount(account)
        }
    }

    fun toggleBalance() {
        _uiState.update { it.copy(showBalance = !it.showBalance) }
    }
}
