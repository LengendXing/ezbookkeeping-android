package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.ui.component.DateRangeSelectionSheet
import com.ezbookkeeping.android.ui.component.ListItemSelectionSheet
import com.ezbookkeeping.android.ui.component.SelectableItem
import com.ezbookkeeping.android.ui.component.TransactionTagSelectionSheet
import com.ezbookkeeping.android.ui.component.TagItem
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.AmountUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(navController: NavController) {
    val vm: TransactionListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val refreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()

    var showSearchBar by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<TransactionEntity?>(null) }
    var showAccountFilter by remember { mutableStateOf(false) }
    var showCategoryFilter by remember { mutableStateOf(false) }
    var showTagFilter by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    // Infinite scroll detection
    LaunchedEffect(listState, state.monthGroups) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible >= totalItems - 3
        }.collect { nearEnd ->
            if (nearEnd && state.hasMore && !state.isLoading) {
                vm.loadMore()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.transactions)) },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) { Icon(Icons.Default.Search, contentDescription = "Search") }
                    IconButton(onClick = { navController.navigate(Routes.TRANSACTION_IMPORT) }) { Icon(Icons.Default.FileDownload, contentDescription = "Import") }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.TRANSACTION_EDIT) }) {
                Icon(Icons.Default.Add, contentDescription = "New transaction")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { vm.refresh() },
            state = refreshState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(Modifier.fillMaxSize()) {
                // Search bar
                if (showSearchBar) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = vm::onSearchQueryChange,
                        placeholder = { Text("Search transaction description") },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        singleLine = true
                    )
                }

                // Type filter chips
                Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = state.typeFilter == null, onClick = { vm.onTypeFilterChange(null) }, label = { Text("All") })
                    FilterChip(selected = state.typeFilter == TransactionType.EXPENSE, onClick = { vm.onTypeFilterChange(TransactionType.EXPENSE) }, label = { Text(stringResource(R.string.expense)) })
                    FilterChip(selected = state.typeFilter == TransactionType.INCOME, onClick = { vm.onTypeFilterChange(TransactionType.INCOME) }, label = { Text(stringResource(R.string.income)) })
                    FilterChip(selected = state.typeFilter == TransactionType.TRANSFER, onClick = { vm.onTypeFilterChange(TransactionType.TRANSFER) }, label = { Text(stringResource(R.string.transfer)) })
                }

                // Advanced filter row
                Row(Modifier.padding(horizontal = 16.dp, vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = state.accountFilter.isNotEmpty(),
                        onClick = { showAccountFilter = true },
                        label = { Text("Accounts${if (state.accountFilter.isNotEmpty()) " (${state.accountFilter.size})" else ""}") },
                        leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    FilterChip(
                        selected = state.categoryFilter.isNotEmpty(),
                        onClick = { showCategoryFilter = true },
                        label = { Text("Categories${if (state.categoryFilter.isNotEmpty()) " (${state.categoryFilter.size})" else ""}") }
                    )
                    FilterChip(
                        selected = state.tagFilter.isNotEmpty(),
                        onClick = { showTagFilter = true },
                        label = { Text("Tags${if (state.tagFilter.isNotEmpty()) " (${state.tagFilter.size})" else ""}") }
                    )
                    FilterChip(
                        selected = state.dateRangeStart.isNotEmpty(),
                        onClick = { showDateRangePicker = true },
                        label = { Text(if (state.dateRangeStart.isNotEmpty()) "${state.dateRangeStart}~${state.dateRangeEnd}" else "Date") }
                    )
                }

                // Transaction list grouped by month
                if (state.isLoading && state.monthGroups.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else if (state.monthGroups.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.no_transactions), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    LazyColumn(Modifier.fillMaxSize(), state = listState) {
                        state.monthGroups.forEach { group ->
                            item(key = "header_${group.yearMonth}") {
                                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                    Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(group.yearMonth, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                        Text("-${AmountUtil.format(group.totalExpense)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                        Spacer(Modifier.width(8.dp))
                                        Text("+${AmountUtil.format(group.totalIncome)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                            items(group.transactions, key = { it.id }) { tx ->
                                TransactionRow(tx, onEdit = { navController.navigate(Routes.TRANSACTION_EDIT + "/${tx.id}") }, onDelete = { showDeleteConfirm = tx })
                            }
                        }
                        if (state.isLoading && state.monthGroups.isNotEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation
    showDeleteConfirm?.let { tx ->
        AlertDialog(onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = { TextButton(onClick = { vm.deleteTransaction(tx); showDeleteConfirm = null }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = null }) { Text(stringResource(R.string.cancel)) } })
    }

    // Account filter
    if (showAccountFilter) {
        val items = state.accounts.map { SelectableItem(id = it.id, name = it.name, subtitle = "${it.currency} · ${it.balance}", color = it.color, group = it.type.name) }
        ListItemSelectionSheet(
            visible = showAccountFilter,
            title = "Filter by Account",
            items = items,
            selectedId = null,
            onDismiss = { showAccountFilter = false },
            onSelect = { vm.toggleAccountFilter(it) }
        )
    }

    // Category filter
    if (showCategoryFilter) {
        val items = state.categories.map { SelectableItem(id = it.id, name = it.name, color = it.color, group = it.type.name) }
        ListItemSelectionSheet(
            visible = showCategoryFilter,
            title = "Filter by Category",
            items = items,
            selectedId = null,
            onDismiss = { showCategoryFilter = false },
            onSelect = { vm.toggleCategoryFilter(it) }
        )
    }

    // Tag filter
    if (showTagFilter) {
        val tagItems = state.tags.map { TagItem(id = it.id, name = it.name, groupId = it.groupId, groupName = state.tagGroups.find { g -> g.id == it.groupId }?.name ?: "") }
        TransactionTagSelectionSheet(
            visible = showTagFilter,
            title = "Filter by Tag",
            tags = tagItems,
            selectedTagIds = state.tagFilter,
            onDismiss = { showTagFilter = false },
            onConfirm = { vm.setTagFilter(it) }
        )
    }

    // Date range picker
    DateRangeSelectionSheet(
        visible = showDateRangePicker,
        initialStartDate = state.dateRangeStart,
        initialEndDate = state.dateRangeEnd,
        onDismiss = { showDateRangePicker = false },
        onSelect = { start, end -> vm.setDateRange(start, end) }
    )

    LaunchedEffect(Unit) { vm.loadTransactions() }
}

@Composable
private fun TransactionRow(tx: TransactionEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    val amountColor = when (tx.type) {
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary
    }
    val prefix = when (tx.type) { TransactionType.EXPENSE -> "-"; TransactionType.INCOME -> "+"; TransactionType.TRANSFER -> "" }

    ListItem(
        headlineContent = { Text(tx.comment ?: tx.type.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(tx.date + (tx.time?.let { " $it" } ?: "")) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$prefix${AmountUtil.format(tx.sourceAmount)}", color = amountColor, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Text("🗑", style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        modifier = Modifier.clickable(onClick = onEdit).padding(horizontal = 8.dp)
    )
}
