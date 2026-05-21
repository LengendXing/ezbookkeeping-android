package com.ezbookkeeping.android.ui.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezbookkeeping.android.data.db.entity.*
import com.ezbookkeeping.android.data.repository.*
import com.ezbookkeeping.android.ui.navigation.AuthState
import com.ezbookkeeping.android.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class FilterParams(
    val txs: List<TransactionEntity>,
    val count: Int,
    val query: String,
    val type: TransactionType?,
    val accountFilter: List<Int>,
    val categoryFilter: List<Int>,
    val tagFilter: List<Int>,
    val dateRangeStart: String,
    val dateRangeEnd: String
)

data class TransactionMonthGroup(
    val yearMonth: String,
    val transactions: List<TransactionEntity>,
    val totalExpense: Double,
    val totalIncome: Double
)

data class TransactionListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val typeFilter: TransactionType? = null,
    val monthGroups: List<TransactionMonthGroup> = emptyList(),
    val hasMore: Boolean = true,
    val accountFilter: List<Int> = emptyList(),
    val categoryFilter: List<Int> = emptyList(),
    val tagFilter: List<Int> = emptyList(),
    val dateRangeStart: String = "",
    val dateRangeEnd: String = "",
    val accounts: List<AccountEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val tags: List<TagEntity> = emptyList(),
    val tagGroups: List<TagGroupEntity> = emptyList()
)

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val accountRepo: AccountRepository,
    private val categoryRepo: CategoryRepository,
    private val tagRepo: TagRepository,
    private val authState: AuthState
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _typeFilter = MutableStateFlow<TransactionType?>(null)
    private val _accountFilter = MutableStateFlow<List<Int>>(emptyList())
    private val _categoryFilter = MutableStateFlow<List<Int>>(emptyList())
    private val _tagFilter = MutableStateFlow<List<Int>>(emptyList())
    private val _dateRangeStart = MutableStateFlow("")
    private val _dateRangeEnd = MutableStateFlow("")
    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private val _displayCount = MutableStateFlow(50)

    init {
        loadPickerData()
        viewModelScope.launch {
            combine(_transactions, _displayCount) { txs, count -> txs to count }
                .combine(_searchQuery) { (txs, count), query -> Triple(txs, count, query) }
                .combine(_typeFilter) { (txs, count, query), type -> FilterParams(txs, count, query, type, _accountFilter.value, _categoryFilter.value, _tagFilter.value, _dateRangeStart.value, _dateRangeEnd.value) }
                .combine(_accountFilter) { params, acctFilter -> params.copy(accountFilter = acctFilter) }
                .combine(_categoryFilter) { params, catFilter -> params.copy(categoryFilter = catFilter) }
                .combine(_tagFilter) { params, tagF -> params.copy(tagFilter = tagF) }
                .combine(_dateRangeStart) { params, dateStart -> params.copy(dateRangeStart = dateStart) }
                .combine(_dateRangeEnd) { params, dateEnd -> params.copy(dateRangeEnd = dateEnd) }
                .collect { p ->
                    var filtered = p.txs
                    if (p.type != null) filtered = filtered.filter { it.type == p.type }
                    if (p.query.isNotBlank()) filtered = filtered.filter { it.comment?.contains(p.query, ignoreCase = true) == true }
                    if (p.accountFilter.isNotEmpty()) filtered = filtered.filter { it.sourceAccountId in p.accountFilter || it.destinationAccountId in p.accountFilter }
                    if (p.categoryFilter.isNotEmpty()) filtered = filtered.filter { it.categoryId in p.categoryFilter }
                    if (p.tagFilter.isNotEmpty()) filtered = filtered.filter { it.tagIds.any { t -> t in p.tagFilter } }
                    if (p.dateRangeStart.isNotBlank()) filtered = filtered.filter { it.date >= p.dateRangeStart }
                    if (p.dateRangeEnd.isNotBlank()) filtered = filtered.filter { it.date <= p.dateRangeEnd }

                    val hasMore = filtered.size > p.count
                    val displayed = filtered.take(p.count)
                    val groups = displayed.groupBy { tx -> tx.date.substring(0, 7) }.map { (ym, list) ->
                        TransactionMonthGroup(
                            yearMonth = ym,
                            transactions = list.sortedByDescending { tx -> tx.date + (tx.time ?: "") },
                            totalExpense = list.filter { tx -> tx.type == TransactionType.EXPENSE }.sumOf { tx -> tx.sourceAmount },
                            totalIncome = list.filter { tx -> tx.type == TransactionType.INCOME }.sumOf { tx -> tx.sourceAmount }
                        )
                    }.sortedByDescending { group -> group.yearMonth }
                    _uiState.update { s ->
                        s.copy(
                            searchQuery = p.query, typeFilter = p.type,
                            accountFilter = p.accountFilter, categoryFilter = p.categoryFilter,
                            tagFilter = p.tagFilter, dateRangeStart = p.dateRangeStart, dateRangeEnd = p.dateRangeEnd,
                            monthGroups = groups, hasMore = hasMore
                        )
                    }
                }
        }
    }

    private fun loadPickerData() {
        val userId = authState.userId
        viewModelScope.launch { accountRepo.getAccounts(userId).collect { list -> _uiState.update { s -> s.copy(accounts = list) } } }
        viewModelScope.launch { categoryRepo.getByUserId(userId).collect { list -> _uiState.update { s -> s.copy(categories = list) } } }
        viewModelScope.launch { tagRepo.getTags(userId).collect { list -> _uiState.update { s -> s.copy(tags = list) } } }
        viewModelScope.launch { tagRepo.getGroups(userId).collect { list -> _uiState.update { s -> s.copy(tagGroups = list) } } }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepo.getByDateRange(authState.userId, DateUtil.yearStart(), DateUtil.yearEnd())
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { list ->
                    _transactions.value = list
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            _displayCount.value = 50
            transactionRepo.getByDateRange(authState.userId, DateUtil.yearStart(), DateUtil.yearEnd())
                .catch { _uiState.update { it.copy(isRefreshing = false) } }
                .collect { list ->
                    _transactions.value = list
                    _uiState.update { it.copy(isRefreshing = false) }
                }
        }
    }

    fun loadMore() { _displayCount.update { it + 50 } }
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun onTypeFilterChange(type: TransactionType?) { _typeFilter.value = type }

    fun toggleAccountFilter(accountId: Int) {
        _accountFilter.update { current -> if (accountId in current) current - accountId else current + accountId }
    }

    fun toggleCategoryFilter(categoryId: Int) {
        _categoryFilter.update { current -> if (categoryId in current) current - categoryId else current + categoryId }
    }

    fun setTagFilter(tagIds: List<Int>) { _tagFilter.value = tagIds }
    fun setDateRange(start: String, end: String) { _dateRangeStart.value = start; _dateRangeEnd.value = end }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            transactionRepo.delete(tx)
            _transactions.value = _transactions.value.filter { it.id != tx.id }
        }
    }
}
