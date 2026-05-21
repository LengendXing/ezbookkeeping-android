package com.ezbookkeeping.android.ui.screen.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.local.UserPreferences
import com.ezbookkeeping.android.ui.navigation.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileUiState(
    val username: String = "", val nickname: String = "", val email: String = "",
    val language: String = "English", val defaultCurrency: String = "CNY",
    val firstDayOfWeek: String = "Monday", val isLoading: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    fun onNicknameChange(v: String) { _uiState.update { it.copy(nickname = v) } }
    fun onEmailChange(v: String) { _uiState.update { it.copy(email = v) } }

    fun save() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
        }
    }
}
