package com.ezbookkeeping.android.ui.screen.transaction

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import com.ezbookkeeping.android.data.db.entity.TransactionType
import javax.inject.Inject

@HiltViewModel
class TransactionImportViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionImportUiState())
    val uiState: StateFlow<TransactionImportUiState> = _uiState.asStateFlow()

    fun setFormat(fmt: ImportFormat) { _uiState.update { it.copy(selectedFormat = fmt) } }

    fun pickFile() {
        // Placeholder: in production this would launch a file picker via ActivityResultContracts
        _uiState.update { it.copy(isLoading = true) }
        // Simulate preview with sample data
        _uiState.update { it.copy(isLoading = false, previewData = listOf(
            ImportPreviewRow("2025-01-15", "Grocery Store", 45.50, TransactionType.EXPENSE),
            ImportPreviewRow("2025-01-16", "Salary", 3000.0, TransactionType.INCOME),
            ImportPreviewRow("2025-01-17", "Gas Station", 55.0, TransactionType.EXPENSE)
        )) }
    }

    fun confirmImport() {
        _uiState.update { it.copy(isImporting = true) }
        // Placeholder: actual import logic would parse file and save to Room
        _uiState.update { it.copy(isImporting = false, importResult = "Successfully imported ${_uiState.value.previewData.size} transactions") }
    }
}
