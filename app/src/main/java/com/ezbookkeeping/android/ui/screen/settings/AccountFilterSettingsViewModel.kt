package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class FilterItem(val id: Int, val name: String, val isSelected: Boolean)

@Stable
data class AccountFilterSettingsUiState(
    val availableAccounts: List<FilterItem> = emptyList(),
    val selectAll: Boolean = true
)

@HiltViewModel
class AccountFilterSettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AccountFilterSettingsUiState(
        availableAccounts = listOf(
            FilterItem(1, "Cash", true),
            FilterItem(2, "Bank Card", true),
            FilterItem(3, "Alipay", true),
            FilterItem(4, "WeChat Pay", true),
            FilterItem(5, "Savings", false)
        )
    ))
    val uiState: StateFlow<AccountFilterSettingsUiState> = _uiState.asStateFlow()

    fun toggleAccount(id: Int) {
        _uiState.update { state ->
            val accounts = state.availableAccounts.map { if (it.id == id) it.copy(isSelected = !it.isSelected) else it }
            state.copy(availableAccounts = accounts, selectAll = accounts.all { it.isSelected })
        }
    }

    fun toggleSelectAll() {
        _uiState.update { state ->
            val newVal = !state.selectAll
            state.copy(availableAccounts = state.availableAccounts.map { it.copy(isSelected = newVal) }, selectAll = newVal)
        }
    }
}
