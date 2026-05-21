package com.ezbookkeeping.android.ui.screen.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AboutUiState(val version: String = "0.2.0", val buildInfo: String = "Phase 3 - Extended Features")

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState = _uiState.asStateFlow()
}
