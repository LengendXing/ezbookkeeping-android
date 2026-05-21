package com.ezbookkeeping.android.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.local.UserPreferences
import com.ezbookkeeping.android.data.repository.UserRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(val serverUrl: String = "", val baseCurrency: String = "USD", val timezone: String = "UTC", val isLoggedOut: Boolean = false)

@HiltViewModel
class SettingsViewModel @Inject constructor(private val prefs: UserPreferences, private val authState: AuthState, private val userRepo: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init { viewModelScope.launch { prefs.serverUrl.collect { url -> _uiState.update { it.copy(serverUrl = url) } } } }

    fun onServerUrlChange(v: String) { _uiState.update { it.copy(serverUrl = v) } }
    fun saveServerUrl() { viewModelScope.launch { prefs.saveServerUrl(_uiState.value.serverUrl) } }

    fun logout() {
        viewModelScope.launch {
            try { userRepo.logout() } catch (_: Exception) { }
            authState.onLogout()
            prefs.clearLogin()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}
