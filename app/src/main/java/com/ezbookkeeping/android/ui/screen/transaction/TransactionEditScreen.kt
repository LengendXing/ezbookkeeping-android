package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.*
import com.ezbookkeeping.android.ui.component.*

enum class TransactionTypeExt { EXPENSE, INCOME, TRANSFER, MODIFY_BALANCE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditScreen(navController: NavController, transactionId: Int? = null) {
    val vm: TransactionEditViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(transactionId) { transactionId?.let { vm.loadTransaction(it) } }
    LaunchedEffect(state.saveSuccess) { if (state.saveSuccess) navController.popBackStack() }

    var showNumberPad by remember { mutableStateOf(false) }
    var showDestNumberPad by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showSourceAccountPicker by remember { mutableStateOf(false) }
    var showDestAccountPicker by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showTagPicker by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showScheduleFreqPicker by remember { mutableStateOf(false) }
    var showScheduleStartPicker by remember { mutableStateOf(false) }
    var showScheduleEndPicker by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf(false) }
    var showAIRecognition by remember { mutableStateOf(false) }
    var showVoiceInput by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (state.isEdit) stringResource(R.string.edit_transaction) else stringResource(R.string.new_transaction)) },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } },
            actions = {
                if (state.isEdit) {
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) { Icon(Icons.Default.MoreVert, contentDescription = "More") }
                        DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Copy Transaction") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                onClick = { showMoreMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Save as Template") },
                                leadingIcon = { Icon(Icons.Default.SaveAlt, contentDescription = null) },
                                onClick = { showMoreMenu = false }
                            )
                        }
                    }
                }
            }
        )
    }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Type selector (4 types including ModifyBalance)
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    TransactionTypeExt.entries.forEach { type ->
                        FilterChip(
                            selected = state.typeExt == type,
                            onClick = { vm.onTypeExtChange(type) },
                            label = {
                                Text(
                                    when (type) {
                                        TransactionTypeExt.EXPENSE -> stringResource(R.string.expense)
                                        TransactionTypeExt.INCOME -> stringResource(R.string.income)
                                        TransactionTypeExt.TRANSFER -> stringResource(R.string.transfer)
                                        TransactionTypeExt.MODIFY_BALANCE -> "Adjust"
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Source amount (clickable to open NumberPad)
            item {
                val label = when (state.typeExt) {
                    TransactionTypeExt.EXPENSE -> "Expense Amount"
                    TransactionTypeExt.INCOME -> "Income Amount"
                    TransactionTypeExt.TRANSFER -> "Transfer Out Amount"
                    TransactionTypeExt.MODIFY_BALANCE -> "Adjust Amount"
                }
                ListItem(
                    headlineContent = { Text(label) },
                    supportingContent = {
                        Text(if (state.sourceAmount.isNotEmpty()) state.sourceAmount else "Tap to enter amount",
                            color = if (state.sourceAmount.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (state.sourceAmount.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.clickable { showNumberPad = true }
                )
            }

            // Destination amount (transfer only)
            if (state.typeExt == TransactionTypeExt.TRANSFER) {
                item {
                    ListItem(
                        headlineContent = { Text("Transfer In Amount") },
                        supportingContent = {
                            Text(if (state.destinationAmount.isNotEmpty()) state.destinationAmount else "Tap to enter amount",
                                color = if (state.destinationAmount.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (state.destinationAmount.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.clickable { showDestNumberPad = true }
                    )
                }
            }

            // Category (using TreeViewSelectionSheet)
            if (state.typeExt != TransactionTypeExt.TRANSFER) {
                item {
                    val selectedCat = state.categories.find { it.id == state.categoryId }
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.categories)) },
                        supportingContent = { Text(selectedCat?.name ?: "Select category") },
                        modifier = Modifier.clickable { showCategoryPicker = true }
                    )
                }
            }

            // Source account (using TwoColumnListItemSelectionSheet)
            item {
                val label = when (state.typeExt) {
                    TransactionTypeExt.TRANSFER -> "Transfer Out Account"
                    TransactionTypeExt.MODIFY_BALANCE -> "Account to Adjust"
                    else -> stringResource(R.string.accounts)
                }
                val selectedAcct = state.accounts.find { it.id == state.sourceAccountId }
                ListItem(
                    headlineContent = { Text(label) },
                    supportingContent = { Text(selectedAcct?.name ?: "Select account") },
                    modifier = Modifier.clickable { showSourceAccountPicker = true }
                )
            }

            // Destination account (transfer only)
            if (state.typeExt == TransactionTypeExt.TRANSFER) {
                item {
                    val selectedDest = state.accounts.find { it.id == state.destinationAccountId }
                    ListItem(
                        headlineContent = { Text("Transfer In Account") },
                        supportingContent = { Text(selectedDest?.name ?: "Select destination") },
                        modifier = Modifier.clickable { showDestAccountPicker = true }
                    )
                }
            }

            // Date + Time (using DateTimeSelectionSheet)
            item {
                val dateDisplay = state.date + if (state.time.isNotBlank()) " ${state.time}" else ""
                ListItem(
                    headlineContent = { Text(stringResource(R.string.date)) },
                    supportingContent = { Text(dateDisplay) },
                    modifier = Modifier.clickable { showDateTimePicker = true }
                )
            }

            // Tags (using TransactionTagSelectionSheet)
            item {
                val selectedTags = state.tags.filter { it.id in state.tagIds }
                ListItem(
                    headlineContent = { Text(stringResource(R.string.tags)) },
                    supportingContent = {
                        if (selectedTags.isEmpty()) Text("Select tags")
                        else Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            selectedTags.forEach { tag -> AssistChip(onClick = { vm.onTagToggle(tag.id) }, label = { Text("#${tag.name}") }) }
                        }
                    },
                    modifier = Modifier.clickable { showTagPicker = true }
                )
            }

            // Comment
            item {
                OutlinedTextField(
                    value = state.comment,
                    onValueChange = vm::onCommentChange,
                    label = { Text(stringResource(R.string.comment)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }

            // Scheduled transaction section
            item {
                HorizontalDivider()
                Text("Schedule", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
            item {
                ListItem(
                    headlineContent = { Text("Frequency") },
                    supportingContent = { Text(state.scheduleFrequency?.label ?: "Not scheduled") },
                    modifier = Modifier.clickable { showScheduleFreqPicker = true }
                )
            }
            if (state.scheduleFrequency != null) {
                item {
                    ListItem(
                        headlineContent = { Text("Start Date") },
                        supportingContent = { Text(state.scheduleStartDate.ifBlank { "Select start date" }) },
                        modifier = Modifier.clickable { showScheduleStartPicker = true }
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("End Date") },
                        supportingContent = { Text(state.scheduleEndDate.ifBlank { "No end date" }) },
                        modifier = Modifier.clickable { showScheduleEndPicker = true }
                    )
                }
            }

            // Advanced features section
            item {
                HorizontalDivider()
                Text("Advanced", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
            item {
                ListItem(
                    headlineContent = { Text("Location") },
                    supportingContent = { Text(if (state.latitude != 0.0 || state.longitude != 0.0) "${state.latitude}, ${state.longitude}" else "Not set") },
                    modifier = Modifier.clickable { showMapPicker = true }
                )
            }
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showAIRecognition = true }, modifier = Modifier.weight(1f)) { Text("AI Scan") }
                    OutlinedButton(onClick = { showVoiceInput = true }, modifier = Modifier.weight(1f)) { Text("Voice") }
                }
            }

            // Error
            if (state.error != null) {
                item { Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }

            // Save
            item {
                Button(
                    onClick = vm::save,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(if (state.isEdit) stringResource(R.string.update) else stringResource(R.string.save))
                    }
                }
            }
        }
    }

    // NumberPad for source amount
    NumberPadSheet(
        visible = showNumberPad,
        initialAmount = state.sourceAmount,
        onDismiss = { showNumberPad = false },
        onConfirm = { vm.onAmountChange(it); showNumberPad = false }
    )

    // NumberPad for destination amount
    NumberPadSheet(
        visible = showDestNumberPad,
        initialAmount = state.destinationAmount,
        onDismiss = { showDestNumberPad = false },
        onConfirm = { vm.onDestinationAmountChange(it); showDestNumberPad = false }
    )

    // Category tree selection
    if (showCategoryPicker) {
        val filtered = state.categories.filter {
            when (state.typeExt) {
                TransactionTypeExt.EXPENSE -> it.type == CategoryType.EXPENSE
                TransactionTypeExt.INCOME -> it.type == CategoryType.INCOME
                TransactionTypeExt.MODIFY_BALANCE -> true
                else -> it.type == CategoryType.TRANSFER
            }
        }
        val treeNodes = filtered.map { TreeNode(id = it.id, name = it.name, color = it.color, parentId = it.parentId) }
        TreeViewSelectionSheet(
            visible = showCategoryPicker,
            nodes = treeNodes,
            selectedId = state.categoryId,
            onDismiss = { showCategoryPicker = false },
            onSelect = { vm.onCategoryChange(it) }
        )
    }

    // Source account selection (grouped by type)
    if (showSourceAccountPicker) {
        val items = state.accounts.map { SelectableItem(id = it.id, name = it.name, subtitle = "${it.currency} · ${it.balance}", color = it.color, group = it.type.name) }
        TwoColumnListItemSelectionSheet(
            visible = showSourceAccountPicker,
            title = "Select Account",
            items = items,
            selectedId = state.sourceAccountId,
            onDismiss = { showSourceAccountPicker = false },
            onSelect = { vm.onSourceAccountChange(it) }
        )
    }

    // Dest account selection
    if (showDestAccountPicker) {
        val items = state.accounts.map { SelectableItem(id = it.id, name = it.name, subtitle = "${it.currency} · ${it.balance}", color = it.color, group = it.type.name) }
        TwoColumnListItemSelectionSheet(
            visible = showDestAccountPicker,
            title = "Transfer In Account",
            items = items,
            selectedId = state.destinationAccountId,
            onDismiss = { showDestAccountPicker = false },
            onSelect = { vm.onDestinationAccountChange(it) }
        )
    }

    // Date + Time picker
    DateTimeSelectionSheet(
        visible = showDateTimePicker,
        initialDate = state.date,
        initialTime = state.time,
        onDismiss = { showDateTimePicker = false },
        onSelect = { date, time -> vm.onDateChange(date); vm.onTimeChange(time) }
    )

    // Tag multi-select
    if (showTagPicker) {
        val tagItems = state.tags.map { TagItem(id = it.id, name = it.name, groupId = it.groupId, groupName = state.tagGroups.find { g -> g.id == it.groupId }?.name ?: "") }
        TransactionTagSelectionSheet(
            visible = showTagPicker,
            tags = tagItems,
            selectedTagIds = state.tagIds,
            onDismiss = { showTagPicker = false },
            onConfirm = { vm.setTagIds(it) }
        )
    }

    // Schedule frequency picker
    ScheduleFrequencySheet(
        visible = showScheduleFreqPicker,
        selected = state.scheduleFrequency ?: ScheduleFrequency.MONTHLY,
        onDismiss = { showScheduleFreqPicker = false },
        onSelect = { vm.setScheduleFrequency(it); showScheduleFreqPicker = false }
    )

    // Schedule start date
    DateSelectionSheet(
        visible = showScheduleStartPicker,
        initialDate = state.scheduleStartDate,
        onDismiss = { showScheduleStartPicker = false },
        onSelect = { vm.setScheduleStartDate(it); showScheduleStartPicker = false }
    )

    // Schedule end date
    DateSelectionSheet(
        visible = showScheduleEndPicker,
        initialDate = state.scheduleEndDate,
        onDismiss = { showScheduleEndPicker = false },
        onSelect = { vm.setScheduleEndDate(it); showScheduleEndPicker = false }
    )

    // Map picker
    MapSheet(visible = showMapPicker, initialLat = state.latitude, initialLng = state.longitude,
        onDismiss = { showMapPicker = false }, onSelect = { lat, lng -> vm.setLocation(lat, lng); showMapPicker = false })

    // AI recognition
    AIImageRecognitionSheet(visible = showAIRecognition, onDismiss = { showAIRecognition = false }, onResult = { comment, amount -> vm.onCommentChange(comment); vm.onAmountChange(amount.toString()); showAIRecognition = false })

    // Voice input
    VoiceTransactionSheet(visible = showVoiceInput, onDismiss = { showVoiceInput = false }, onResult = { comment, amount -> vm.onCommentChange(comment); vm.onAmountChange(amount.toString()); showVoiceInput = false })
}
