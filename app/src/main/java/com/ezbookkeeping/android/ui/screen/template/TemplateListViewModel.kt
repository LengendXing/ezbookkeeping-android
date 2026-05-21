package com.ezbookkeeping.android.ui.screen.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.TemplateEntity
import com.ezbookkeeping.android.data.repository.TemplateRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TemplateListUiState(val templates: List<TemplateEntity> = emptyList(), val isLoading: Boolean = false)

@HiltViewModel
class TemplateListViewModel @Inject constructor(private val templateRepo: TemplateRepository, private val authState: AuthState) : ViewModel() {
    private val _uiState = MutableStateFlow(TemplateListUiState())
    val uiState: StateFlow<TemplateListUiState> = _uiState.asStateFlow()

    init { loadTemplates() }

    fun loadTemplates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            templateRepo.getByUserId(authState.userId).catch { _uiState.update { it.copy(isLoading = false) } }.collect { list -> _uiState.update { it.copy(templates = list, isLoading = false) } }
        }
    }
}
