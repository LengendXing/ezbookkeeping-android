package com.ezbookkeeping.android.ui.screen.user

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SessionListUiState(val sessions: List<SessionInfo> = emptyList())

@HiltViewModel
class SessionListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SessionListUiState(listOf(
        SessionInfo("1", "Android Device", "Now", true)
    )))
    val uiState: StateFlow<SessionListUiState> = _uiState.asStateFlow()

    fun revokeSession(id: String) { _uiState.update { it.copy(sessions = it.sessions.filter { s -> s.id != id }) } }
    fun revokeAllOtherSessions() { _uiState.update { it.copy(sessions = it.sessions.filter { s -> s.isCurrent }) } }
}
