package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.runtime.Stable
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

@Stable
data class TransactionImportUiState(
    val isLoading: Boolean = false,
    val previewData: List<ImportPreviewRow> = emptyList(),
    val importResult: String? = null,
    val isImporting: Boolean = false,
    val selectedFormat: ImportFormat = ImportFormat.CSV,
    val step: ImportStep = ImportStep.UPLOAD,
    val columnMapping: Map<String, String> = emptyMap()
)

enum class ImportFormat(val label: String) { CSV("CSV"), OFX("OFX"), QIF("QIF") }
enum class ImportStep { UPLOAD, DEFINE_COLUMNS, REVIEW, EXECUTE }

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
            // Step indicator
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ImportStep.entries.forEach { step ->
                    FilterChip(selected = state.step == step, onClick = { if (step.ordinal < state.step.ordinal) vm.goToStep(step) },
                        label = { Text(step.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) })
                }
            }

            when (state.step) {
                ImportStep.UPLOAD -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImportFormat.entries.forEach { fmt ->
                            FilterChip(selected = state.selectedFormat == fmt, onClick = { vm.setFormat(fmt) }, label = { Text(fmt.label) })
                        }
                    }
                    OutlinedButton(onClick = vm::pickFile, modifier = Modifier.fillMaxWidth()) { Text("Select File to Import") }
                    if (state.previewData.isNotEmpty()) {
                        Button(onClick = { vm.goToStep(ImportStep.DEFINE_COLUMNS) }, modifier = Modifier.fillMaxWidth()) { Text("Next: Define Columns") }
                    }
                }
                ImportStep.DEFINE_COLUMNS -> {
                    Text("Column Mapping", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    val columns = listOf("Date", "Amount", "Type", "Category", "Comment", "Account")
                    columns.forEach { col ->
                        val mapped = state.columnMapping[col] ?: ""
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(col, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            var selected by remember(mapped) { mutableStateOf(mapped) }
                            OutlinedTextField(value = selected, onValueChange = { selected = it; vm.setColumnMapping(col, it) }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Column #") })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { vm.goToStep(ImportStep.UPLOAD) }, modifier = Modifier.weight(1f)) { Text("Back") }
                        Button(onClick = { vm.goToStep(ImportStep.REVIEW) }, modifier = Modifier.weight(1f)) { Text("Next: Review") }
                    }
                }
                ImportStep.REVIEW -> {
                    if (state.previewData.isNotEmpty()) {
                        Text("Preview (${state.previewData.size} transactions)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        LazyColumn(Modifier.weight(1f)) {
                            items(state.previewData) { row ->
                                ListItem(
                                    headlineContent = { Text(row.comment, maxLines = 1) },
                                    supportingContent = { Text(row.date) },
                                    trailingContent = { Text(AmountUtil.format(row.amount), fontWeight = FontWeight.Bold) }
                                )
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { vm.goToStep(ImportStep.DEFINE_COLUMNS) }, modifier = Modifier.weight(1f)) { Text("Back") }
                        Button(onClick = { vm.goToStep(ImportStep.EXECUTE); vm.confirmImport() }, modifier = Modifier.weight(1f), enabled = !state.isImporting) {
                            if (state.isImporting) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary) else Text("Import")
                        }
                    }
                }
                ImportStep.EXECUTE -> {
                    if (state.importResult != null) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Text(state.importResult!!, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    if (state.isLoading) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    }
                    Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Done") }
                }
            }
        }
    }
}
