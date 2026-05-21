package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Stable
data class PageSettingsUiState(
    val defaultLandingPage: String = "Home",
    val showOverviewCard: Boolean = true,
    val showTransactionAmount: Boolean = true,
    val showTransactionComment: Boolean = true,
    val transactionPageSize: Int = 50,
    val availablePages: List<String> = listOf("Home", "Accounts", "Statistics", "Details")
)

@HiltViewModel
class PageSettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PageSettingsUiState())
    val uiState: StateFlow<PageSettingsUiState> = _uiState.asStateFlow()

    fun setLandingPage(page: String) { _uiState.update { it.copy(defaultLandingPage = page) } }
    fun toggleOverviewCard(show: Boolean) { _uiState.update { it.copy(showOverviewCard = show) } }
    fun toggleTransactionAmount(show: Boolean) { _uiState.update { it.copy(showTransactionAmount = show) } }
    fun toggleTransactionComment(show: Boolean) { _uiState.update { it.copy(showTransactionComment = show) } }
    fun setPageSize(size: Int) { _uiState.update { it.copy(transactionPageSize = size) } }
}
