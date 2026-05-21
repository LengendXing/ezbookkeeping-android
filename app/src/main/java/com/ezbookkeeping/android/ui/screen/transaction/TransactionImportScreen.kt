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
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.util.AmountUtil

data class ImportPreviewRow(val date: String, val comment: String, val amount: Double, val type: TransactionType)

data class TransactionImportUiState(
    val isLoading: Boolean = false,
    val previewData: List<ImportPreviewRow> = emptyList(),
    val importResult: String? = null,
    val isImporting: Boolean = false,
    val selectedFormat: ImportFormat = ImportFormat.CSV
)

enum class ImportFormat(val label: String) { CSV("CSV"), OFX("OFX"), QIF("QIF") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionImportScreen(navController: NavController) {
    val vm: TransactionImportViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Import Transactions") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Format selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ImportFormat.values().forEach { fmt ->
                    FilterChip(selected = state.selectedFormat == fmt, onClick = { vm.setFormat(fmt) }, label = { Text(fmt.label) })
                }
            }

            // Pick file button
            OutlinedButton(onClick = vm::pickFile, modifier = Modifier.fillMaxWidth()) {
                Text("Select File to Import")
            }

            // Preview
            if (state.previewData.isNotEmpty()) {
                Text("Preview (${state.previewData.size} transactions)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                LazyColumn(Modifier.weight(1f)) {
                    items(state.previewData) { row ->
                        ListItem(
                            headlineContent = { Text(row.comment, maxLines = 1) },
                            supportingContent = { Text(row.date) },
                            trailingContent = { Text(AmountUtil.format(row.amount), fontWeight = FontWeight.Bold, color = when(row.type) { TransactionType.EXPENSE -> MaterialTheme.colorScheme.error; TransactionType.INCOME -> MaterialTheme.colorScheme.primary; TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary }) }
                        )
                    }
                }
                Button(onClick = vm::confirmImport, modifier = Modifier.fillMaxWidth(), enabled = !state.isImporting) {
                    if (state.isImporting) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Import ${state.previewData.size} Transactions")
                }
            }

            if (state.importResult != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(state.importResult!!, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
        }
    }
}
