package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Stable
data class TextSizeSettingsUiState(
    val textSizeScale: Float = 1.0f,
    val previewText: String = "Sample transaction amount: ¥1,234.56"
)

@HiltViewModel
class TextSizeSettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TextSizeSettingsUiState())
    val uiState: StateFlow<TextSizeSettingsUiState> = _uiState.asStateFlow()

    fun setTextScale(scale: Float) { _uiState.update { it.copy(textSizeScale = scale) } }
}
