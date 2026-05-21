package com.ezbookkeeping.android.ui.screen.user

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DataManagementUiState(val isExporting: Boolean = false, val isImporting: Boolean = false)

@HiltViewModel
class DataManagementViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DataManagementUiState())
    val uiState: StateFlow<DataManagementUiState> = _uiState.asStateFlow()

    fun exportData() { _uiState.update { it.copy(isExporting = true) } }
    fun clearData() { /* TODO: clear Room DB */ }
}
