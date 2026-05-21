package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.ui.component.NumberPadSheet
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReconciliationScreen(navController: NavController) {
    val vm: ReconciliationViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var showAccountPicker by remember { mutableStateOf(false) }
    var showOpeningBalancePad by remember { mutableStateOf(false) }
    var showClosingBalancePad by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.loadAccounts() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Reconciliation") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Account selector
            ListItem(
                headlineContent = { Text("Account") },
                supportingContent = { Text(state.selectedAccountName ?: "Select account") },
                modifier = Modifier.clickable { showAccountPicker = true }
            )

            // Opening / Closing balance
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Balance Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth().clickable { showOpeningBalancePad = true }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Opening Balance")
                        Text(if (state.openingBalance >= 0) AmountUtil.format(state.openingBalance) else "Tap to set",
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.openingBalance < 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                    }
                    Row(Modifier.fillMaxWidth().clickable { showClosingBalancePad = true }, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Closing Balance (Statement)")
                        Text(if (state.closingBalance >= 0) AmountUtil.format(state.closingBalance) else "Tap to set",
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.closingBalance < 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Book Balance")
                        Text(AmountUtil.format(state.bookBalance), fontWeight = FontWeight.SemiBold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Difference", fontWeight = FontWeight.SemiBold)
                        Text(AmountUtil.format(state.difference), fontWeight = FontWeight.SemiBold,
                            color = if (state.difference == 0.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Adjust balance button
            if (state.difference != 0.0 && state.selectedAccountId != null) {
                OutlinedButton(onClick = { vm.createAdjustBalance(navController) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Create Adjust Balance Transaction")
                }
            }

            if (state.rows.isNotEmpty()) {
                Text("Transactions (${state.rows.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(state.rows) { row ->
                        ListItem(
                            headlineContent = { Text(row.description) },
                            supportingContent = { Text(row.date) },
                            trailingContent = {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Stmt: ${AmountUtil.format(row.statementAmount)}", style = MaterialTheme.typography.bodySmall)
                                    Text("Book: ${AmountUtil.format(row.bookAmount)}", style = MaterialTheme.typography.bodySmall)
                                    if (!row.isMatched) {
                                        Text("Diff: ${AmountUtil.format(row.difference)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            },
                            leadingContent = {
                                if (row.isMatched) Text("OK", color = MaterialTheme.colorScheme.primary)
                                else Text("X", color = MaterialTheme.colorScheme.error)
                            }
                        )
                    }
                }

                Button(onClick = vm::reconcile, modifier = Modifier.fillMaxWidth(), enabled = !state.isReconciling) {
                    if (state.isReconciling) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Complete Reconciliation")
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }

            if (state.result != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(state.result!!, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }

    // Account picker
    if (showAccountPicker) {
        val items = state.accounts.map { com.ezbookkeeping.android.ui.component.SelectableItem(id = it.id, name = it.name, subtitle = it.currency, color = it.color, group = it.type.name) }
        com.ezbookkeeping.android.ui.component.TwoColumnListItemSelectionSheet(
            visible = showAccountPicker,
            title = "Select Account",
            items = items,
            selectedId = state.selectedAccountId,
            onDismiss = { showAccountPicker = false },
            onSelect = { vm.selectAccount(it); showAccountPicker = false }
        )
    }

    // Opening balance numpad
    NumberPadSheet(
        visible = showOpeningBalancePad,
        initialAmount = if (state.openingBalance >= 0) AmountUtil.format(state.openingBalance) else "",
        onDismiss = { showOpeningBalancePad = false },
        onConfirm = { vm.setOpeningBalance(it); showOpeningBalancePad = false }
    )

    // Closing balance numpad
    NumberPadSheet(
        visible = showClosingBalancePad,
        initialAmount = if (state.closingBalance >= 0) AmountUtil.format(state.closingBalance) else "",
        onDismiss = { showClosingBalancePad = false },
        onConfirm = { vm.setClosingBalance(it); showClosingBalancePad = false }
    )
}
