package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditScreen(navController: NavController, transactionId: Int? = null) {
    val vm: TransactionEditViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(transactionId) { transactionId?.let { vm.loadTransaction(it) } }
    LaunchedEffect(state.saveSuccess) { if (state.saveSuccess) navController.popBackStack() }

    var showCategoryPicker by remember { mutableStateOf(false) }
    var showSourceAccountPicker by remember { mutableStateOf(false) }
    var showDestAccountPicker by remember { mutableStateOf(false) }
    var showTagPicker by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (state.isEdit) stringResource(R.string.edit_transaction) else stringResource(R.string.new_transaction)) },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } }
        )
    }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Type selector
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TransactionType.entries.forEach { type ->
                        FilterChip(
                            selected = state.type == type,
                            onClick = { vm.onTypeChange(type) },
                            label = { Text(when (type) { TransactionType.EXPENSE -> stringResource(R.string.expense); TransactionType.INCOME -> stringResource(R.string.income); TransactionType.TRANSFER -> stringResource(R.string.transfer) }) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            // Source amount
            item {
                OutlinedTextField(value = state.sourceAmount, onValueChange = vm::onAmountChange,
                    label = { Text(when (state.type) { TransactionType.EXPENSE -> "Expense Amount"; TransactionType.INCOME -> "Income Amount"; TransactionType.TRANSFER -> "Transfer Out Amount" }) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            // Destination amount (transfer only)
            if (state.type == TransactionType.TRANSFER) {
                item {
                    OutlinedTextField(value = state.destinationAmount, onValueChange = vm::onDestinationAmountChange,
                        label = { Text("Transfer In Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
            }
            // Category
            item {
                val selectedCat = state.categories.find { it.id == state.categoryId }
                ListItem(headlineContent = { Text(stringResource(R.string.categories)) },
                    supportingContent = { Text(selectedCat?.name ?: "Select category") },
                    modifier = Modifier.clickable { showCategoryPicker = true })
            }
            // Source account
            item {
                val selectedAcct = state.accounts.find { it.id == state.sourceAccountId }
                ListItem(headlineContent = { Text(if (state.type == TransactionType.TRANSFER) "Transfer Out Account" else stringResource(R.string.accounts)) },
                    supportingContent = { Text(selectedAcct?.name ?: "Select account") },
                    modifier = Modifier.clickable { showSourceAccountPicker = true })
            }
            // Destination account (transfer only)
            if (state.type == TransactionType.TRANSFER) {
                item {
                    val selectedDest = state.accounts.find { it.id == state.destinationAccountId }
                    ListItem(headlineContent = { Text("Transfer In Account") },
                        supportingContent = { Text(selectedDest?.name ?: "Select destination") },
                        modifier = Modifier.clickable { showDestAccountPicker = true })
                }
            }
            // Date
            item { OutlinedTextField(value = state.date, onValueChange = vm::onDateChange, label = { Text(stringResource(R.string.date)) }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            // Time
            item { OutlinedTextField(value = state.time, onValueChange = vm::onTimeChange, label = { Text("Time (HH:mm)") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            // Tags
            item {
                val selectedTags = state.tags.filter { it.id in state.tagIds }
                ListItem(headlineContent = { Text(stringResource(R.string.tags)) },
                    supportingContent = {
                        if (selectedTags.isEmpty()) Text("Select tags")
                        else Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            selectedTags.forEach { tag -> AssistChip(onClick = { vm.onTagToggle(tag.id) }, label = { Text("#${tag.name}") }) }
                        }
                    },
                    modifier = Modifier.clickable { showTagPicker = true })
            }
            // Comment
            item { OutlinedTextField(value = state.comment, onValueChange = vm::onCommentChange, label = { Text(stringResource(R.string.comment)) }, modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4) }
            // Error
            if (state.error != null) item { Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            // Save
            item {
                Button(onClick = vm::save, modifier = Modifier.fillMaxWidth().height(48.dp), enabled = !state.isLoading) {
                    if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text(if (state.isEdit) stringResource(R.string.update) else stringResource(R.string.save))
                }
            }
        }
    }

    // Category picker
    if (showCategoryPicker) {
        val filtered = state.categories.filter { when (state.type) { TransactionType.EXPENSE -> it.type == CategoryType.EXPENSE; TransactionType.INCOME -> it.type == CategoryType.INCOME; TransactionType.TRANSFER -> it.type == CategoryType.TRANSFER } }
        val primaryCats = filtered.filter { it.parentId == null }
        AlertDialog(onDismissRequest = { showCategoryPicker = false }, title = { Text(stringResource(R.string.categories)) },
            text = { LazyColumn { items(primaryCats) { parent ->
                Column { PickerItem(parent.name, parent.color, state.categoryId == parent.id) { vm.onCategoryChange(parent.id); showCategoryPicker = false }
                    filtered.filter { it.parentId == parent.id }.forEach { child ->
                        PickerItem(child.name, child.color, state.categoryId == child.id, indent = 24.dp) { vm.onCategoryChange(child.id); showCategoryPicker = false } } }
            } } },
            confirmButton = { TextButton(onClick = { showCategoryPicker = false }) { Text(stringResource(R.string.cancel)) } })
    }
    // Source account picker
    if (showSourceAccountPicker) {
        AccountPickerDialog(state.accounts, state.sourceAccountId, stringResource(R.string.accounts)) { vm.onSourceAccountChange(it); showSourceAccountPicker = false }
    }
    // Dest account picker
    if (showDestAccountPicker) {
        AccountPickerDialog(state.accounts, state.destinationAccountId, "Transfer In Account") { vm.onDestinationAccountChange(it); showDestAccountPicker = false }
    }
    // Tag picker
    if (showTagPicker) {
        AlertDialog(onDismissRequest = { showTagPicker = false }, title = { Text(stringResource(R.string.tags)) },
            text = { LazyColumn {
                state.tagGroups.forEach { group ->
                    val groupTags = state.tags.filter { it.groupId == group.id }
                    if (groupTags.isNotEmpty()) {
                        item { Text(group.name, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) }
                        items(groupTags) { tag ->
                            val selected = tag.id in state.tagIds
                            ListItem(headlineContent = { Text("#${tag.name}") }, trailingContent = { if (selected) Text("✓", color = MaterialTheme.colorScheme.primary) }, modifier = Modifier.clickable { vm.onTagToggle(tag.id) })
                        }
                    }
                }
            } },
            confirmButton = { TextButton(onClick = { showTagPicker = false }) { Text("Done") } })
    }
}

@Composable
private fun PickerItem(name: String, colorStr: String, selected: Boolean, indent: Dp = 0.dp, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = indent, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        if (colorStr.isNotEmpty()) {
            Surface(modifier = Modifier.size(20.dp), shape = MaterialTheme.shapes.extraSmall,
                color = try { Color(android.graphics.Color.parseColor(colorStr)) } catch (_: Exception) { MaterialTheme.colorScheme.primary }) {}
            Spacer(Modifier.width(8.dp))
        }
        Text(name, modifier = Modifier.weight(1f))
        if (selected) Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AccountPickerDialog(accounts: List<AccountEntity>, selectedId: Int?, title: String, onSelect: (Int) -> Unit) {
    AlertDialog(onDismissRequest = {}, title = { Text(title) },
        text = { LazyColumn { items(accounts) { acct ->
            ListItem(headlineContent = { Text(acct.name) }, supportingContent = { Text("${acct.currency} · ${acct.balance}") },
                trailingContent = { if (acct.id == selectedId) Text("✓", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.clickable { onSelect(acct.id) })
        } } },
        confirmButton = { TextButton(onClick = {}) { Text("Cancel") } })
}
