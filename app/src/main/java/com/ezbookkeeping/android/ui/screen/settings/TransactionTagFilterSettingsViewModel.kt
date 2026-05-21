package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Stable
data class TransactionTagFilterSettingsUiState(
    val availableTags: List<FilterItem> = emptyList(),
    val selectAll: Boolean = true
)

@HiltViewModel
class TransactionTagFilterSettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionTagFilterSettingsUiState(
        availableTags = listOf(
            FilterItem(1, "Work", true), FilterItem(2, "Personal", true),
            FilterItem(3, "Travel", true), FilterItem(4, "Health", true),
            FilterItem(5, "Education", true), FilterItem(6, "Investment", true)
        )
    ))
    val uiState: StateFlow<TransactionTagFilterSettingsUiState> = _uiState.asStateFlow()

    fun toggleTag(id: Int) {
        _uiState.update { state ->
            val tags = state.availableTags.map { if (it.id == id) it.copy(isSelected = !it.isSelected) else it }
            state.copy(availableTags = tags, selectAll = tags.all { it.isSelected })
        }
    }

    fun toggleSelectAll() {
        _uiState.update { state ->
            val newVal = !state.selectAll
            state.copy(availableTags = state.availableTags.map { it.copy(isSelected = newVal) }, selectAll = newVal)
        }
    }
}
