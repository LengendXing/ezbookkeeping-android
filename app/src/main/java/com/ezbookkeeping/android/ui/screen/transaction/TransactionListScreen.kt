package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.AmountUtil
import kotlinx.coroutines.launch

data class TransactionMonthGroup(val yearMonth: String, val transactions: List<TransactionEntity>, val totalExpense: Double, val totalIncome: Double)

data class TransactionListUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val typeFilter: TransactionType? = null,
    val monthGroups: List<TransactionMonthGroup> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(navController: NavController) {
    val vm: TransactionListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showSearchBar by remember { mutableStateOf(false) }
    var showTypeFilter by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<TransactionEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.transactions)) },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) { Icon(Icons.Default.Search, contentDescription = "Search") }
                    IconButton(onClick = { showTypeFilter = true }) { Text("Filter", style = MaterialTheme.typography.labelMedium) }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.TRANSACTION_EDIT) }) {
                Icon(Icons.Default.Add, contentDescription = "New transaction")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
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

            // Transaction list grouped by month
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (state.monthGroups.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.no_transactions), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    state.monthGroups.forEach { group ->
                        // Month header
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
                        // Transactions in this month
                        items(group.transactions, key = { it.id }) { tx ->
                            TransactionRow(tx, onEdit = { navController.navigate(Routes.TRANSACTION_EDIT + "/${tx.id}") }, onDelete = { showDeleteConfirm = tx })
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

    // Type filter dialog (unused since chips are shown, kept for future extension)
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
