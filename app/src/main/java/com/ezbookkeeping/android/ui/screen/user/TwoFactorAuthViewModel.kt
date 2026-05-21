package com.ezbookkeeping.android.ui.screen.user

import androidx.lifecycle.ViewModel
import com.ezbookkeeping.android.service.TotpService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TwoFactorAuthUiState(val isEnabled: Boolean = false, val backupCodes: List<String> = emptyList())

@HiltViewModel
class TwoFactorAuthViewModel @Inject constructor(private val totpService: TotpService) : ViewModel() {
    private val _uiState = MutableStateFlow(TwoFactorAuthUiState())
    val uiState: StateFlow<TwoFactorAuthUiState> = _uiState.asStateFlow()

    fun enable2fa() { _uiState.update { it.copy(isEnabled = true, backupCodes = (1..6).map { generateRandomCode() }) } }
    fun disable2fa() { _uiState.update { it.copy(isEnabled = false, backupCodes = emptyList()) } }

    private fun generateRandomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8).map { chars.random() }.joinToString("")
    }
}
