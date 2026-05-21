package com.ezbookkeeping.android.ui.screen.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagEditScreen(navController: NavController, tagId: Int? = null) {
    val vm: TagEditViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    LaunchedEffect(tagId) { tagId?.let { vm.loadTag(it) } }
    LaunchedEffect(state.saveSuccess) { if (state.saveSuccess) navController.popBackStack() }
    var showGroupPicker by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = { Text(if (state.isEdit) "Edit Tag" else "New Tag") },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = state.name, onValueChange = vm::onNameChange, label = { Text("Tag Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            // Group picker
            ListItem(headlineContent = { Text("Tag Group") },
                supportingContent = { Text(state.tagGroups.find { it.id == state.groupId }?.name ?: "Select group") },
                modifier = Modifier.clickable { showGroupPicker = true })
            if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.weight(1f))
            Button(onClick = vm::save, modifier = Modifier.fillMaxWidth().height(48.dp), enabled = !state.isLoading) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text(if (state.isEdit) stringResource(R.string.update) else stringResource(R.string.save))
            }
        }
    }

    if (showGroupPicker) {
        AlertDialog(onDismissRequest = { showGroupPicker = false }, title = { Text("Tag Group") },
            text = {
                LazyColumn {
                    items(state.tagGroups) { group ->
                        ListItem(headlineContent = { Text(group.name) }, trailingContent = { if (group.id == state.groupId) Text("✓") },
                            modifier = Modifier.clickable { vm.onGroupChange(group.id); showGroupPicker = false })
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showGroupPicker = false }) { Text(stringResource(R.string.cancel)) } })
    }
}
