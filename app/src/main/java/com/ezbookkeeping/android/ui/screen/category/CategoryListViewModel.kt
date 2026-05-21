package com.ezbookkeeping.android.ui.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.CategoryEntity
import com.ezbookkeeping.android.data.repository.CategoryRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryListUiState(val categories: List<CategoryEntity> = emptyList(), val isLoading: Boolean = false)

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    init { loadCategories() }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoryRepo.getByUserId(authState.userId).catch { _uiState.update { it.copy(isLoading = false) } }.collect { list -> _uiState.update { it.copy(categories = list, isLoading = false) } }
        }
    }
}
