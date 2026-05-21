package com.ezbookkeeping.android.ui.screen.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.AccountEntity
import com.ezbookkeeping.android.data.db.entity.AccountType
import com.ezbookkeeping.android.ui.component.PasswordInputSheet
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.AmountUtil
import com.ezbookkeeping.android.util.parseColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListScreen(navController: NavController) {
    val vm: AccountListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var accountToDelete by remember { mutableStateOf<AccountEntity?>(null) }
    var accountActions by remember { mutableStateOf<AccountEntity?>(null) }
    var showPasswordConfirm by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.accounts)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.ACCOUNT_EDIT) }) {
                Icon(Icons.Default.Add, contentDescription = "Add account")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(vertical = 8.dp)) {
                item { AccountOverviewCard(state.accounts, state.showBalance, vm::toggleBalance) }

                val assetAccounts = state.accounts.filter { it.type == AccountType.ASSET }
                val liabilityAccounts = state.accounts.filter { it.type == AccountType.LIABILITY }

                if (assetAccounts.isNotEmpty()) {
                    item { GroupHeader("Assets", assetAccounts.sumOf { it.balance }, assetAccounts.firstOrNull()?.currency ?: "", state.showBalance) }
                    items(assetAccounts, key = { it.id }) { account ->
                        AccountRow(account, state.showBalance,
                            onClick = { navController.navigate(Routes.ACCOUNT_EDIT + "/${account.id}") },
                            onLongPress = { accountActions = account })
                    }
                }
                if (liabilityAccounts.isNotEmpty()) {
                    item { GroupHeader("Liabilities", liabilityAccounts.sumOf { it.balance }, liabilityAccounts.firstOrNull()?.currency ?: "", state.showBalance) }
                    items(liabilityAccounts, key = { it.id }) { account ->
                        AccountRow(account, state.showBalance,
                            onClick = { navController.navigate(Routes.ACCOUNT_EDIT + "/${account.id}") },
                            onLongPress = { accountActions = account })
                    }
                }
                if (state.accounts.isEmpty()) {
                    item { Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { Text("No accounts yet", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                }
            }
        }
    }

    // Account actions menu
    accountActions?.let { account ->
        AlertDialog(onDismissRequest = { accountActions = null },
            title = { Text(account.name) },
            text = {
                Column {
                    TextButton(onClick = { accountActions = null; navController.navigate(Routes.ACCOUNT_EDIT + "/${account.id}") }) { Text("Edit Account") }
                    TextButton(onClick = { accountActions = null; navController.navigate(Routes.MOVE_ALL_TRANSACTIONS + "/${account.id}") }) { Text("Move All Transactions") }
                    TextButton(onClick = {
                        pendingAction = { vm.clearAllTransactions(account); accountActions = null }
                        accountActions = null; showPasswordConfirm = true
                    }) { Text("Clear All Transactions", color = MaterialTheme.colorScheme.error) }
                    TextButton(onClick = { accountActions = null; navController.navigate(Routes.RECONCILIATION) }) { Text("Reconcile") }
                    HorizontalDivider()
                    TextButton(onClick = { accountToDelete = account; accountActions = null }) { Text("Delete Account", color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = { TextButton(onClick = { accountActions = null }) { Text("Cancel") } }
        )
    }

    // Delete confirmation
    if (accountToDelete != null) {
        AlertDialog(onDismissRequest = { accountToDelete = null },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete \"${accountToDelete?.name}\"?") },
            confirmButton = { TextButton(onClick = { accountToDelete?.let { vm.deleteAccount(it) }; accountToDelete = null }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { accountToDelete = null }) { Text(stringResource(R.string.cancel)) } })
    }

    // Password confirmation for dangerous actions
    PasswordInputSheet(
        visible = showPasswordConfirm,
        title = "Confirm Action",
        message = "Enter your password to continue",
        onDismiss = { showPasswordConfirm = false; pendingAction = null },
        onConfirm = { showPasswordConfirm = false; pendingAction?.invoke(); pendingAction = null }
    )
}

@Composable
private fun AccountOverviewCard(accounts: List<AccountEntity>, showBalance: Boolean, onToggleBalance: () -> Unit) {
    val totalAssets = accounts.filter { it.type == AccountType.ASSET }.sumOf { it.balance }
    val totalLiabilities = accounts.filter { it.type == AccountType.LIABILITY }.sumOf { it.balance }
    val netAssets = totalAssets - totalLiabilities
    val primaryCurrency = accounts.firstOrNull()?.currency ?: "CNY"

    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Net Assets", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                IconButton(onClick = onToggleBalance, modifier = Modifier.size(24.dp)) {
                    Icon(if (showBalance) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                }
            }
            Text(if (showBalance) "$primaryCurrency ${AmountUtil.format(netAssets)}" else "****",
                style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Assets", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    Text(if (showBalance) "$primaryCurrency ${AmountUtil.format(totalAssets)}" else "****",
                        style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Liabilities", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    Text(if (showBalance) "$primaryCurrency ${AmountUtil.format(totalLiabilities)}" else "****",
                        style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable private fun GroupHeader(title: String, total: Double, currency: String, showBalance: Boolean) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        Text(if (showBalance) "$currency ${AmountUtil.format(total)}" else "****", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable private fun AccountRow(account: AccountEntity, showBalance: Boolean, onClick: () -> Unit, onLongPress: () -> Unit) {
    val isAsset = account.type == AccountType.ASSET
    val amountColor = if (isAsset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val bgColor = parseColor(account.color)
    ListItem(
        headlineContent = { Text(account.name, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(account.currency, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent = { Box(Modifier.size(40.dp).clip(MaterialTheme.shapes.medium).background(bgColor), contentAlignment = Alignment.Center) { Text(account.icon, fontSize = 18.sp, color = Color.White) } },
        trailingContent = { Text(if (showBalance) AmountUtil.format(account.balance) else "****", fontWeight = FontWeight.Bold, color = amountColor) },
        modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongPress).padding(horizontal = 8.dp)
    )
}

