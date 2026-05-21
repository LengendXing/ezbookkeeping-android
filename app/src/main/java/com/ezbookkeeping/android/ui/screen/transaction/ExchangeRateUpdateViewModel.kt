package com.ezbookkeeping.android.ui.screen.transaction

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ExchangeRateUpdateUiState(
    val isLoading: Boolean = false,
    val isAutoUpdate: Boolean = false,
    val updateFrequency: UpdateFrequency = UpdateFrequency.DAILY,
    val lastUpdated: String? = null,
    val isUpdating: Boolean = false,
    val updateResult: String? = null,
    val availableSources: List<RateSource> = listOf(
        RateSource("ecb", "European Central Bank"),
        RateSource("frb", "Federal Reserve Board"),
        RateSource("boe", "Bank of England")
    ),
    val selectedSource: String = "ecb"
)

enum class UpdateFrequency(val label: String) { DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly") }

data class RateSource(val id: String, val name: String)

@HiltViewModel
class ExchangeRateUpdateViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ExchangeRateUpdateUiState())
    val uiState: StateFlow<ExchangeRateUpdateUiState> = _uiState.asStateFlow()

    fun setAutoUpdate(enabled: Boolean) { _uiState.update { it.copy(isAutoUpdate = enabled) } }
    fun setFrequency(freq: UpdateFrequency) { _uiState.update { it.copy(updateFrequency = freq) } }
    fun setSource(id: String) { _uiState.update { it.copy(selectedSource = id) } }

    fun updateNow() {
        _uiState.update { it.copy(isUpdating = true) }
        _uiState.update { it.copy(isUpdating = false, lastUpdated = "Just now", updateResult = "Exchange rates updated successfully") }
    }
}
