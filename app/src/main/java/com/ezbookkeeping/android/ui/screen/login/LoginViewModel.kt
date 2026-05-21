package com.ezbookkeeping.android.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.local.UserPreferences
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(val username: String = "", val password: String = "", val twoFactorCode: String = "", val isLoading: Boolean = false, val error: String? = null, val loginSuccess: Boolean = false)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: EZBookkeepingApi,
    private val authState: AuthState,
    private val prefs: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(v: String) { _uiState.update { it.copy(username = v, error = null) } }
    fun onPasswordChange(v: String) { _uiState.update { it.copy(password = v, error = null) } }
    fun onTwoFactorCodeChange(v: String) { _uiState.update { it.copy(twoFactorCode = v, error = null) } }

    fun login() {
        val s = _uiState.value
        if (s.username.isBlank() || s.password.isBlank()) { _uiState.update { it.copy(error = "Username and password required") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val resp = api.login(com.ezbookkeeping.android.data.remote.dto.LoginRequest(s.username, s.password, s.twoFactorCode.ifBlank { null }))
                val data = resp.data
                if (data != null) {
                    authState.onLogin(0, data.accessToken, data.refreshToken)
                    prefs.saveLogin(0, data.accessToken, data.refreshToken)
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = resp.message.ifBlank { "Login failed" }) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Network error") }
            }
        }
    }
}
