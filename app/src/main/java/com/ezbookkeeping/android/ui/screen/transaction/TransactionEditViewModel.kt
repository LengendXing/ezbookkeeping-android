package com.ezbookkeeping.android.ui.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.*
import com.ezbookkeeping.android.data.repository.*
import com.ezbookkeeping.android.ui.navigation.AuthState
import com.ezbookkeeping.android.ui.component.ScheduleFrequency
import com.ezbookkeeping.android.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionEditUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val typeExt: TransactionTypeExt = TransactionTypeExt.EXPENSE,
    val sourceAmount: String = "",
    val destinationAmount: String = "",
    val categoryId: Int? = null,
    val sourceAccountId: Int = 0,
    val destinationAccountId: Int? = null,
    val tagIds: List<Int> = emptyList(),
    val comment: String = "",
    val date: String = DateUtil.today(),
    val time: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val isEdit: Boolean = false,
    val accounts: List<AccountEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val tags: List<TagEntity> = emptyList(),
    val tagGroups: List<TagGroupEntity> = emptyList(),
    val scheduleFrequency: ScheduleFrequency? = null,
    val scheduleStartDate: String = "",
    val scheduleEndDate: String = ""
)

@HiltViewModel
class TransactionEditViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val accountRepo: AccountRepository,
    private val categoryRepo: CategoryRepository,
    private val tagRepo: TagRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionEditUiState())
    val uiState: StateFlow<TransactionEditUiState> = _uiState.asStateFlow()

    init { loadPickerData() }

    private fun loadPickerData() {
        val userId = authState.userId
        viewModelScope.launch { accountRepo.getAccounts(userId).collect { list -> _uiState.update { it.copy(accounts = list) } } }
        viewModelScope.launch { categoryRepo.getByUserId(userId).collect { list -> _uiState.update { it.copy(categories = list) } } }
        viewModelScope.launch { tagRepo.getTags(userId).collect { list -> _uiState.update { it.copy(tags = list) } } }
        viewModelScope.launch { tagRepo.getGroups(userId).collect { list -> _uiState.update { it.copy(tagGroups = list) } } }
    }

    fun onTypeExtChange(t: TransactionTypeExt) {
        val transactionType = when (t) {
            TransactionTypeExt.EXPENSE -> TransactionType.EXPENSE
            TransactionTypeExt.INCOME -> TransactionType.INCOME
            TransactionTypeExt.TRANSFER -> TransactionType.TRANSFER
            TransactionTypeExt.MODIFY_BALANCE -> TransactionType.EXPENSE
        }
        _uiState.update { it.copy(typeExt = t, type = transactionType, categoryId = null, destinationAccountId = null) }
    }

    fun onAmountChange(v: String) { _uiState.update { it.copy(sourceAmount = v, error = null) } }
    fun onDestinationAmountChange(v: String) { _uiState.update { it.copy(destinationAmount = v) } }
    fun onCategoryChange(id: Int?) { _uiState.update { it.copy(categoryId = id) } }
    fun onSourceAccountChange(id: Int) { _uiState.update { it.copy(sourceAccountId = id) } }
    fun onDestinationAccountChange(id: Int?) { _uiState.update { it.copy(destinationAccountId = id) } }
    fun onCommentChange(v: String) { _uiState.update { it.copy(comment = v) } }
    fun onDateChange(v: String) { _uiState.update { it.copy(date = v) } }
    fun onTimeChange(v: String) { _uiState.update { it.copy(time = v) } }
    fun onTagToggle(tagId: Int) {
        val current = _uiState.value.tagIds
        _uiState.update { it.copy(tagIds = if (current.contains(tagId)) current - tagId else current + tagId) }
    }
    fun setTagIds(ids: List<Int>) { _uiState.update { it.copy(tagIds = ids) } }
    fun setScheduleFrequency(f: ScheduleFrequency) { _uiState.update { it.copy(scheduleFrequency = f) } }
    fun setScheduleStartDate(d: String) { _uiState.update { it.copy(scheduleStartDate = d) } }
    fun setScheduleEndDate(d: String) { _uiState.update { it.copy(scheduleEndDate = d) } }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isEdit = true) }
            transactionRepo.getById(id).first()?.let { tx ->
                val typeExt = when (tx.type) {
                    TransactionType.EXPENSE -> TransactionTypeExt.EXPENSE
                    TransactionType.INCOME -> TransactionTypeExt.INCOME
                    TransactionType.TRANSFER -> TransactionTypeExt.TRANSFER
                }
                _uiState.update {
                    it.copy(type = tx.type, typeExt = typeExt, sourceAccountId = tx.sourceAccountId,
                        destinationAccountId = tx.destinationAccountId,
                        sourceAmount = tx.sourceAmount.toString(),
                        destinationAmount = tx.destinationAmount?.toString() ?: "",
                        comment = tx.comment ?: "", date = tx.date, time = tx.time ?: "",
                        categoryId = tx.categoryId, tagIds = tx.tagIds)
                }
            }
        }
    }

    fun save() {
        val s = _uiState.value
        val amount = s.sourceAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) { _uiState.update { it.copy(error = "Invalid amount") }; return }
        if (s.sourceAccountId == 0) { _uiState.update { it.copy(error = "Please select an account") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val entity = TransactionEntity(
                    id = if (s.isEdit) 0 else System.currentTimeMillis().hashCode(),
                    userId = authState.userId,
                    sourceAccountId = s.sourceAccountId,
                    destinationAccountId = if (s.type == TransactionType.TRANSFER) s.destinationAccountId else null,
                    sourceAmount = amount,
                    destinationAmount = if (s.type == TransactionType.TRANSFER) s.destinationAmount.toDoubleOrNull() else null,
                    type = s.type,
                    categoryId = s.categoryId,
                    tagIds = s.tagIds,
                    comment = s.comment.ifBlank { null },
                    date = s.date,
                    time = s.time.ifBlank { null }
                )
                transactionRepo.upsert(entity)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) { _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
