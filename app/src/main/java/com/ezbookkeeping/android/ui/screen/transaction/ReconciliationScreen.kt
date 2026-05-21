package com.ezbookkeeping.android.ui.screen.transaction

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
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReconciliationScreen(navController: NavController) {
    val vm: ReconciliationViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(Unit) { vm.loadSampleData() }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Reconciliation") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Balance Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Statement Balance")
                        Text(AmountUtil.format(state.statementBalance), fontWeight = FontWeight.SemiBold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Book Balance")
                        Text(AmountUtil.format(state.bookBalance), fontWeight = FontWeight.SemiBold)
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Difference", fontWeight = FontWeight.SemiBold)
                        Text(AmountUtil.format(state.difference), fontWeight = FontWeight.SemiBold,
                            color = if (state.difference == 0.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
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
                                if (row.isMatched) Text("✓", color = MaterialTheme.colorScheme.primary)
                                else Text("✗", color = MaterialTheme.colorScheme.error)
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
}
