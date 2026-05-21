package com.ezbookkeeping.android.ui.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DisplayOrderItem(val id: Int, val name: String, val order: Int)

data class DisplayOrderSettingsUiState(
    val accountOrder: List<DisplayOrderItem> = emptyList(),
    val categoryOrder: List<DisplayOrderItem> = emptyList(),
    val activeTab: DisplayTab = DisplayTab.ACCOUNTS
)

enum class DisplayTab(val label: String) { ACCOUNTS("Accounts"), CATEGORIES("Categories") }

@HiltViewModel
class DisplayOrderSettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DisplayOrderSettingsUiState(
        accountOrder = listOf(
            DisplayOrderItem(1, "Cash", 1), DisplayOrderItem(2, "Bank Card", 2),
            DisplayOrderItem(3, "Alipay", 3), DisplayOrderItem(4, "WeChat Pay", 4)
        ),
        categoryOrder = listOf(
            DisplayOrderItem(5, "Food", 1), DisplayOrderItem(6, "Transport", 2),
            DisplayOrderItem(7, "Shopping", 3), DisplayOrderItem(8, "Housing", 4)
        )
    ))
    val uiState: StateFlow<DisplayOrderSettingsUiState> = _uiState.asStateFlow()

    fun setTab(tab: DisplayTab) { _uiState.update { it.copy(activeTab = tab) } }

    fun moveUp(id: Int) {
        _uiState.update { state ->
            val list = if (state.activeTab == DisplayTab.ACCOUNTS) state.accountOrder else state.categoryOrder
            val idx = list.indexOfFirst { it.id == id }
            if (idx <= 0) return@update state
            val reordered = list.toMutableList()
            val prev = reordered[idx - 1].copy(order = reordered[idx].order)
            val curr = reordered[idx].copy(order = reordered[idx - 1].order)
            reordered[idx - 1] = curr
            reordered[idx] = prev
            if (state.activeTab == DisplayTab.ACCOUNTS) state.copy(accountOrder = reordered) else state.copy(categoryOrder = reordered)
        }
    }

    fun moveDown(id: Int) {
        _uiState.update { state ->
            val list = if (state.activeTab == DisplayTab.ACCOUNTS) state.accountOrder else state.categoryOrder
            val idx = list.indexOfFirst { it.id == id }
            if (idx < 0 || idx >= list.size - 1) return@update state
            val reordered = list.toMutableList()
            val next = reordered[idx + 1].copy(order = reordered[idx].order)
            val curr = reordered[idx].copy(order = reordered[idx + 1].order)
            reordered[idx] = next
            reordered[idx + 1] = curr
            if (state.activeTab == DisplayTab.ACCOUNTS) state.copy(accountOrder = reordered) else state.copy(categoryOrder = reordered)
        }
    }
}
