package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Stable
data class CategoryFilterSettingsUiState(
    val expenseCategories: List<FilterItem> = emptyList(),
    val incomeCategories: List<FilterItem> = emptyList(),
    val selectAll: Boolean = true
)

@HiltViewModel
class CategoryFilterSettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryFilterSettingsUiState(
        expenseCategories = listOf(
            FilterItem(1, "Food", true), FilterItem(2, "Transport", true),
            FilterItem(3, "Shopping", true), FilterItem(4, "Housing", true),
            FilterItem(5, "Entertainment", true), FilterItem(6, "Healthcare", true)
        ),
        incomeCategories = listOf(
            FilterItem(7, "Salary", true), FilterItem(8, "Bonus", true),
            FilterItem(9, "Investment", true), FilterItem(10, "Part-time", true)
        )
    ))
    val uiState: StateFlow<CategoryFilterSettingsUiState> = _uiState.asStateFlow()

    fun toggleCategory(id: Int) {
        _uiState.update { state ->
            val expense = state.expenseCategories.map { if (it.id == id) it.copy(isSelected = !it.isSelected) else it }
            val income = state.incomeCategories.map { if (it.id == id) it.copy(isSelected = !it.isSelected) else it }
            val all = expense + income
            state.copy(expenseCategories = expense, incomeCategories = income, selectAll = all.all { it.isSelected })
        }
    }

    fun toggleSelectAll() {
        _uiState.update { state ->
            val newVal = !state.selectAll
            state.copy(
                expenseCategories = state.expenseCategories.map { it.copy(isSelected = newVal) },
                incomeCategories = state.incomeCategories.map { it.copy(isSelected = newVal) },
                selectAll = newVal
            )
        }
    }
}
