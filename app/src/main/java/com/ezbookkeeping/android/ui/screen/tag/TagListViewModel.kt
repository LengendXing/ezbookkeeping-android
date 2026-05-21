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

@HiltViewModel
class TagListViewModel @Inject constructor(
    private val tagRepo: TagRepository,
    private val authState: AuthState
) : ViewModel() {
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

    fun deleteTag(tag: TagEntity) {
        viewModelScope.launch { tagRepo.deleteTag(tag) }
    }

    fun deleteGroup(group: TagGroupEntity) {
        viewModelScope.launch { tagRepo.deleteGroup(group) }
    }

    fun addGroup(name: String) {
        viewModelScope.launch {
            val maxOrder = _uiState.value.groups.maxOfOrNull { it.order } ?: 0
            tagRepo.upsertGroup(TagGroupEntity(
                id = 0,
                userId = authState.userId,
                name = name,
                order = maxOrder + 1
            ))
        }
    }
}
