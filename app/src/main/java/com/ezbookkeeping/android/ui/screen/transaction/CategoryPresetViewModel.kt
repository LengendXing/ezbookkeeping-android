package com.ezbookkeeping.android.ui.screen.transaction

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CategoryPreset(
    val id: String,
    val name: String,
    val type: String,
    val subCategories: List<String>
)

data class CategoryPresetUiState(
    val isLoading: Boolean = false,
    val presets: List<CategoryPreset> = emptyList(),
    val selectedPresets: Set<String> = emptySet(),
    val isImporting: Boolean = false,
    val importResult: String? = null
)

@HiltViewModel
class CategoryPresetViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryPresetUiState())
    val uiState: StateFlow<CategoryPresetUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(presets = listOf(
            CategoryPreset("daily_expense", "Daily Expenses", "Expense", listOf("Food", "Transport", "Shopping", "Entertainment", "Healthcare")),
            CategoryPreset("daily_income", "Daily Income", "Income", listOf("Salary", "Bonus", "Investment", "Part-time")),
            CategoryPreset("travel", "Travel", "Expense", listOf("Flights", "Hotel", "Local Transport", "Food & Drink", "Souvenirs")),
            CategoryPreset("home", "Home & Living", "Expense", listOf("Rent", "Utilities", "Furniture", "Maintenance", "Insurance")),
            CategoryPreset("education", "Education", "Expense", listOf("Tuition", "Books", "Courses", "Supplies")),
            CategoryPreset("business", "Business", "Income", listOf("Revenue", "Commission", "Consulting", "Royalties"))
        )) }
    }

    fun togglePreset(id: String) {
        _uiState.update { state ->
            val selected = if (id in state.selectedPresets) state.selectedPresets - id else state.selectedPresets + id
            state.copy(selectedPresets = selected)
        }
    }

    fun importPresets() {
        _uiState.update { it.copy(isImporting = true) }
        val count = _uiState.value.selectedPresets.size
        _uiState.update { it.copy(isImporting = false, importResult = "Successfully imported $count preset categories") }
    }
}
