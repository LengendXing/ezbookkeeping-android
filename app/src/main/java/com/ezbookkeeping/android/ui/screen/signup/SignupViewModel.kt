package com.ezbookkeeping.android.ui.screen.signup

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.remote.api.EZBookkeepingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
data class SignupUiState(val username: String = "", val password: String = "", val confirmPassword: String = "", val email: String = "", val nickname: String = "", val isLoading: Boolean = false, val error: String? = null, val signupSuccess: Boolean = false)

@HiltViewModel
class SignupViewModel @Inject constructor(private val api: EZBookkeepingApi) : ViewModel() {
    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onUsernameChange(v: String) { _uiState.update { it.copy(username = v, error = null) } }
    fun onPasswordChange(v: String) { _uiState.update { it.copy(password = v, error = null) } }
    fun onConfirmPasswordChange(v: String) { _uiState.update { it.copy(confirmPassword = v, error = null) } }
    fun onEmailChange(v: String) { _uiState.update { it.copy(email = v, error = null) } }
    fun onNicknameChange(v: String) { _uiState.update { it.copy(nickname = v, error = null) } }

    fun signup() {
        val s = _uiState.value
        if (s.username.isBlank() || s.password.isBlank() || s.email.isBlank()) { _uiState.update { it.copy(error = "All fields required") }; return }
        if (s.password != s.confirmPassword) { _uiState.update { it.copy(error = "Passwords don't match") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val resp = api.signup(com.ezbookkeeping.android.data.remote.dto.SignupRequest(s.username, s.password, s.email, s.nickname.ifBlank { null }))
                if (resp.data != null) _uiState.update { it.copy(isLoading = false, signupSuccess = true) }
                else _uiState.update { it.copy(isLoading = false, error = resp.message.ifBlank { "Signup failed" }) }
            } catch (e: Exception) { _uiState.update { it.copy(isLoading = false, error = e.message ?: "Network error") } }
        }
    }
}
