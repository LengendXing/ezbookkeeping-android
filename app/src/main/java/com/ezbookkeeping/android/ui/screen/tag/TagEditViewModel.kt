package com.ezbookkeeping.android.ui.screen.tag

import androidx.compose.runtime.Stable
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

@Stable
data class TagEditUiState(
    val name: String = "", val groupId: Int = 0, val tagId: Int = 0,
    val tagGroups: List<TagGroupEntity> = emptyList(),
    val isEdit: Boolean = false, val isLoading: Boolean = false,
    val error: String? = null, val saveSuccess: Boolean = false
)

@HiltViewModel
class TagEditViewModel @Inject constructor(
    private val tagRepo: TagRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(TagEditUiState())
    val uiState: StateFlow<TagEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tagRepo.getGroups(authState.userId).distinctUntilChanged().collect { groups -> _uiState.update { it.copy(tagGroups = groups) } }
        }
    }

    fun onNameChange(v: String) { _uiState.update { it.copy(name = v, error = null) } }
    fun onGroupChange(id: Int) { _uiState.update { it.copy(groupId = id) } }

    fun loadTag(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEdit = true, tagId = id) }
            tagRepo.getTagById(id).first()?.let { tag ->
                _uiState.update { it.copy(name = tag.name, groupId = tag.groupId) }
            }
        }
    }

    fun save() {
        val s = _uiState.value
        if (s.name.isBlank()) { _uiState.update { it.copy(error = "Name required") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                tagRepo.upsertTag(TagEntity(
                    id = if (s.isEdit) s.tagId else System.currentTimeMillis().hashCode(),
                    userId = authState.userId, groupId = s.groupId, name = s.name
                ))
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) { _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
