package com.ezbookkeeping.android.ui.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.data.remote.dto.CreateTransactionRequest
import com.ezbookkeeping.android.data.repository.AccountRepository
import com.ezbookkeeping.android.data.repository.CategoryRepository
import com.ezbookkeeping.android.data.repository.TransactionRepository
import com.ezbookkeeping.android.ui.navigation.AuthState
import com.ezbookkeeping.android.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionEditUiState(val type: TransactionType = TransactionType.EXPENSE, val sourceAccountId: Int = 0, val destinationAccountId: Int? = null, val sourceAmount: String = "", val destinationAmount: String = "", val categoryId: Int? = null, val tagIds: List<Int> = emptyList(), val comment: String = "", val date: String = DateUtil.today(), val time: String = "", val isLoading: Boolean = false, val error: String? = null, val saveSuccess: Boolean = false, val isEdit: Boolean = false)

@HiltViewModel
class TransactionEditViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val accountRepo: AccountRepository,
    private val categoryRepo: CategoryRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionEditUiState())
    val uiState: StateFlow<TransactionEditUiState> = _uiState.asStateFlow()

    fun onTypeChange(t: TransactionType) { _uiState.update { it.copy(type = t) } }
    fun onAmountChange(v: String) { _uiState.update { it.copy(sourceAmount = v, error = null) } }
    fun onCommentChange(v: String) { _uiState.update { it.copy(comment = v) } }
    fun onDateChange(v: String) { _uiState.update { it.copy(date = v) } }
    fun onSourceAccountChange(id: Int) { _uiState.update { it.copy(sourceAccountId = id) } }
    fun onCategoryChange(id: Int?) { _uiState.update { it.copy(categoryId = id) } }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEdit = true) }
            transactionRepo.getById(id).first()?.let { tx ->
                _uiState.update { it.copy(type = tx.type, sourceAccountId = tx.sourceAccountId, destinationAccountId = tx.destinationAccountId, sourceAmount = tx.sourceAmount.toString(), comment = tx.comment ?: "", date = tx.date, time = tx.time ?: "", categoryId = tx.categoryId) }
            }
        }
    }

    fun save() {
        val s = _uiState.value
        val amount = s.sourceAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) { _uiState.update { it.copy(error = "Invalid amount") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val request = CreateTransactionRequest(sourceAccountId = s.sourceAccountId, destinationAccountId = s.destinationAccountId, sourceAmount = amount, destinationAmount = s.destinationAmount.toDoubleOrNull(), type = s.type.name.lowercase(), categoryId = s.categoryId, tagIds = s.tagIds, comment = s.comment.ifBlank { null }, date = s.date, time = s.time.ifBlank { null })
                if (s.isEdit) { /* update via api */ } else transactionRepo.createRemoteTransaction(request)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) { _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
