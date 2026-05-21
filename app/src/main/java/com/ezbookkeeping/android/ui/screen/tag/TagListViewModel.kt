package com.ezbookkeeping.android.ui.screen.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.TagEntity
import com.ezbookkeeping.android.data.db.entity.TagGroupEntity
import com.ezbookkeeping.android.data.repository.TagRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TagListUiState(val groups: List<TagGroupEntity> = emptyList(), val tags: List<TagEntity> = emptyList(), val isLoading: Boolean = false)

@HiltViewModel
class TagListViewModel @Inject constructor(private val tagRepo: TagRepository, private val authState: AuthState) : ViewModel() {
    private val _uiState = MutableStateFlow(TagListUiState())
    val uiState: StateFlow<TagListUiState> = _uiState.asStateFlow()

    init { loadTags() }

    fun loadTags() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(tagRepo.getGroups(authState.userId), tagRepo.getTags(authState.userId)) { groups, tags ->
                TagListUiState(groups = groups, tags = tags, isLoading = false)
            }.catch { _uiState.update { it.copy(isLoading = false) } }.collect { state -> _uiState.update { state } }
        }
    }
}
