package com.ezbookkeeping.android.ui.screen.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.ExchangeRateEntity
import com.ezbookkeeping.android.data.repository.ExchangeRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExchangeRateUiState(val rates: List<ExchangeRateEntity> = emptyList(), val isLoading: Boolean = false)

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(private val rateRepo: ExchangeRateRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ExchangeRateUiState())
    val uiState: StateFlow<ExchangeRateUiState> = _uiState.asStateFlow()

    init { loadRates() }

    fun loadRates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            rateRepo.getAll().catch { _uiState.update { it.copy(isLoading = false) } }.collect { list -> _uiState.update { it.copy(rates = list, isLoading = false) } }
        }
    }

    fun refresh() { viewModelScope.launch { try { val resp = rateRepo.fetchRemoteRates(); resp.data?.let { rateRepo.upsertAll(emptyList()) } } catch (_: Exception) { } } }
}
