package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.local.UserPreferences
import com.ezbookkeeping.android.data.repository.UserRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class SettingsUiState(
    val serverUrl: String = "", val isLoggedOut: Boolean = false, val isStandalone: Boolean = true,
    val isDarkTheme: Boolean = false, val themeColor: String = "Default", val timezone: String = "System Default",
    val isAppLockEnabled: Boolean = false, val exchangeRateLastUpdate: String = "",
    val isAutoUpdateExchangeRates: Boolean = false, val showAccountBalance: Boolean = true,
    val isAnimationEnabled: Boolean = true, val isSwipeBackEnabled: Boolean = true, val version: String = "0.7.0"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val authState: AuthState,
    private val userRepo: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { prefs.serverUrl.distinctUntilChanged().collect { url -> _uiState.update { it.copy(serverUrl = url) } } }
        _uiState.update { it.copy(isStandalone = authState.isStandalone) }
    }

    fun onServerUrlChange(v: String) { _uiState.update { it.copy(serverUrl = v) } }
    fun onDarkThemeToggle(v: Boolean) { _uiState.update { it.copy(isDarkTheme = v) } }
    fun onAutoUpdateExchangeRatesToggle(v: Boolean) { _uiState.update { it.copy(isAutoUpdateExchangeRates = v) } }
    fun onShowAccountBalanceToggle(v: Boolean) { _uiState.update { it.copy(showAccountBalance = v) } }
    fun onAnimationToggle(v: Boolean) { _uiState.update { it.copy(isAnimationEnabled = v) } }
    fun onSwipeBackToggle(v: Boolean) { _uiState.update { it.copy(isSwipeBackEnabled = v) } }

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
