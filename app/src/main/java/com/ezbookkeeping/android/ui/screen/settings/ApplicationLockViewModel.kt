package com.ezbookkeeping.android.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.LockType
import com.ezbookkeeping.android.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationLockViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(ApplicationLockUiState())
    val uiState: StateFlow<ApplicationLockUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.lockType.distinctUntilChanged().collect { type -> _uiState.update { it.copy(currentLockType = type) } }
        }
    }

    fun setLockType(type: LockType) {
        viewModelScope.launch { prefs.setLockType(type) }
    }

    fun startSettingPin() {
        _uiState.update { it.copy(isSettingPin = true, isSettingPassword = false, inputCode = "", confirmCode = "", error = null) }
    }

    fun startSettingPassword() {
        _uiState.update { it.copy(isSettingPin = false, isSettingPassword = true, inputCode = "", confirmCode = "", error = null) }
    }

    fun onInputCodeChange(code: String) {
        _uiState.update { it.copy(inputCode = code, error = null) }
    }

    fun confirmFirstPin() {
        _uiState.update { it.copy(confirmCode = _uiState.value.inputCode, inputCode = "", error = null) }
    }

    fun confirmPin() {
        val state = _uiState.value
        if (state.inputCode == state.confirmCode) {
            viewModelScope.launch {
                prefs.setLockType(LockType.PIN)
                prefs.setLockCode(state.inputCode)
                _uiState.update { it.copy(isSettingPin = false, inputCode = "", confirmCode = "") }
            }
        } else {
            _uiState.update { it.copy(error = "PINs do not match", inputCode = "", confirmCode = "") }
        }
    }

    fun confirmFirstPassword() {
        _uiState.update { it.copy(confirmCode = _uiState.value.inputCode, inputCode = "", error = null) }
    }

    fun confirmPassword() {
        val state = _uiState.value
        if (state.inputCode == state.confirmCode) {
            viewModelScope.launch {
                prefs.setLockType(LockType.PASSWORD)
                prefs.setLockCode(state.inputCode)
                _uiState.update { it.copy(isSettingPassword = false, inputCode = "", confirmCode = "") }
            }
        } else {
            _uiState.update { it.copy(error = "Passwords do not match", inputCode = "", confirmCode = "") }
        }
    }

    fun removeLock() {
        viewModelScope.launch {
            prefs.setLockType(LockType.NONE)
            prefs.setLockCode("")
            _uiState.update { it.copy(isSettingPin = false, isSettingPassword = false, inputCode = "", confirmCode = "") }
        }
    }
}
