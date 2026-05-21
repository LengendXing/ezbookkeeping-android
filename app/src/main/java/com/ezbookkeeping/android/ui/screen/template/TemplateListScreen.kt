package com.ezbookkeeping.android.ui.screen.template

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.ezbookkeeping.android.data.db.entity.TemplateEntity
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.AmountUtil

data class TemplateListUiState(val templates: List<TemplateEntity> = emptyList(), val isLoading: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateListScreen(navController: NavController) {
    val vm: TemplateListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var templateToDelete by remember { mutableStateOf<TemplateEntity?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.templates)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.TRANSACTION_EDIT) }) {
                Icon(Icons.Default.Add, contentDescription = "New from template")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (state.templates.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No templates yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(vertical = 4.dp)) {
                items(state.templates, key = { it.id }) { tmpl ->
                    TemplateRow(
                        template = tmpl,
                        onQuickCreate = { navController.navigate(Routes.TRANSACTION_EDIT) },
                        onLongPress = { templateToDelete = tmpl }
                    )
                }
            }
        }
    }

    if (templateToDelete != null) {
        AlertDialog(
            onDismissRequest = { templateToDelete = null },
            title = { Text("Delete Template") },
            text = { Text("Delete template \"${templateToDelete?.name}\"?") },
            confirmButton = {
                TextButton(onClick = { templateToDelete?.let { vm.deleteTemplate(it) }; templateToDelete = null })
                { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { templateToDelete = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TemplateRow(template: TemplateEntity, onQuickCreate: () -> Unit, onLongPress: () -> Unit) {
    val typeLabel = when (template.type) {
        TransactionType.EXPENSE -> "Expense"
        TransactionType.INCOME -> "Income"
        TransactionType.TRANSFER -> "Transfer"
    }
    val typeColor = when (template.type) {
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
        TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary
    }

    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            Modifier.combinedClickable(onClick = onQuickCreate, onLongClick = onLongPress).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(template.name, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(typeLabel, style = MaterialTheme.typography.bodySmall, color = typeColor)
                    template.comment?.let { if (it.isNotBlank()) {
                        Text(" · $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }}
                }
            }
            Text(AmountUtil.format(template.amount), fontWeight = FontWeight.Bold, color = typeColor)
        }
    }
}
