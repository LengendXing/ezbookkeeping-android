package com.ezbookkeeping.android.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.local.UserPreferences
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import com.ezbookkeeping.android.data.remote.dto.LoginRequest
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "", val password: String = "", val twoFaCode: String = "",
    val isLoading: Boolean = false, val error: String? = null, val tips: String? = null,
    val loginSuccess: Boolean = false, val show2faSheet: Boolean = false,
    val twoFaType: String = "passcode", val tempToken: String = "", val version: String = "0.3.0"
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: EZBookkeepingApi,
    private val authState: AuthState,
    private val prefs: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun isStandaloneMode(): Boolean = authState.isStandalone || authState.serverUrl.isBlank()
    fun isInputEmpty(): Boolean = _uiState.value.username.isBlank() || _uiState.value.password.isBlank()

    fun onUsernameChange(v: String) { _uiState.update { it.copy(username = v.trim(), error = null, show2faSheet = false) } }
    fun onPasswordChange(v: String) { _uiState.update { it.copy(password = v, error = null, show2faSheet = false, tempToken = "") } }
    fun onTwoFaCodeChange(v: String) { _uiState.update { it.copy(twoFaCode = v, error = null) } }

    fun loginStandalone() {
        authState.loginStandalone()
        viewModelScope.launch { prefs.saveLogin(1, "local", "local") }
        _uiState.update { it.copy(loginSuccess = true) }
    }

    fun login() {
        val s = _uiState.value
        if (s.username.isBlank()) { _uiState.update { it.copy(error = "Username cannot be blank") }; return }
        if (s.password.isBlank()) { _uiState.update { it.copy(error = "Password cannot be blank") }; return }
        if (s.tempToken.isNotBlank()) { _uiState.update { it.copy(show2faSheet = true) }; return }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val resp = api.login(LoginRequest(s.username, s.password, null))
                val data = resp.data
                if (data != null) {
                    if (resp.code == 200 && data.accessToken.isNotEmpty()) {
                        authState.loginServer(0, data.accessToken, data.refreshToken, authState.serverUrl)
                        prefs.saveLogin(0, data.accessToken, data.refreshToken)
                        _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = resp.message.ifBlank { "Login failed" }) }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = resp.message.ifBlank { "Login failed" }) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Cannot connect to server. Use Quick Start for standalone mode.") }
            }
        }
    }

    fun verify2fa() {
        val s = _uiState.value
        if (s.twoFaCode.isBlank()) { _uiState.update { it.copy(error = if (s.twoFaType == "passcode") "Passcode cannot be blank" else "Backup code cannot be blank") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val resp = api.login(LoginRequest(s.username, s.password, s.twoFaCode))
                val data = resp.data
                if (data != null) {
                    authState.loginServer(0, data.accessToken, data.refreshToken, authState.serverUrl)
                    prefs.saveLogin(0, data.accessToken, data.refreshToken)
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true, show2faSheet = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = resp.message.ifBlank { "Verification failed" }) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Network error") }
            }
        }
    }

    fun switch2faType() { _uiState.update { it.copy(twoFaType = if (it.twoFaType == "passcode") "backupcode" else "passcode", twoFaCode = "") } }
    fun dismiss2fa() { _uiState.update { it.copy(show2faSheet = false) } }
}
