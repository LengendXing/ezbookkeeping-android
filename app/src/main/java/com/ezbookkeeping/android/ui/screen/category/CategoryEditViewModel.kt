package com.ezbookkeeping.android.ui.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.CategoryEntity
import com.ezbookkeeping.android.data.db.entity.CategoryType
import com.ezbookkeeping.android.data.repository.CategoryRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryEditUiState(
    val name: String = "", val type: CategoryType = CategoryType.EXPENSE,
    val icon: String = "label", val color: String = "#F44336",
    val parentId: Int? = null, val categoryId: Int = 0,
    val parentCategories: List<CategoryEntity> = emptyList(),
    val isEdit: Boolean = false, val isLoading: Boolean = false,
    val error: String? = null, val saveSuccess: Boolean = false
)

@HiltViewModel
class CategoryEditViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryEditUiState())
    val uiState: StateFlow<CategoryEditUiState> = _uiState.asStateFlow()

    init { loadParentCategories() }

    private fun loadParentCategories() {
        viewModelScope.launch {
            categoryRepo.getByUserId(authState.userId).collect { all ->
                val parents = all.filter { it.parentId == null }
                _uiState.update { it.copy(parentCategories = parents) }
            }
        }
    }

    fun onNameChange(v: String) { _uiState.update { it.copy(name = v, error = null) } }
    fun onTypeChange(v: CategoryType) { _uiState.update { it.copy(type = v, parentId = null) } }
    fun onColorChange(v: String) { _uiState.update { it.copy(color = v) } }
    fun onParentChange(id: Int?) { _uiState.update { it.copy(parentId = id) } }

    fun loadCategory(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEdit = true, categoryId = id) }
            categoryRepo.getById(id).first()?.let { c ->
                _uiState.update { it.copy(name = c.name, type = c.type, icon = c.icon, color = c.color, parentId = c.parentId) }
            }
        }
    }

    fun save() {
        val s = _uiState.value
        if (s.name.isBlank()) { _uiState.update { it.copy(error = "Name required") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                categoryRepo.upsert(CategoryEntity(
                    id = if (s.isEdit) s.categoryId else System.currentTimeMillis().hashCode(),
                    userId = authState.userId, type = s.type, parentId = s.parentId,
                    name = s.name, icon = s.icon, color = s.color
                ))
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) { _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
