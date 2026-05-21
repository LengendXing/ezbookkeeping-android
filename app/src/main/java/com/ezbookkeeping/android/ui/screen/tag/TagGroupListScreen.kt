package com.ezbookkeeping.android.ui.screen.tag

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.ezbookkeeping.android.data.db.entity.TagGroupEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagGroupListScreen(navController: NavController) {
    val vm: TagListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var groupToDelete by remember { mutableStateOf<TagGroupEntity?>(null) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Tag Groups") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { showAddDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = "Add group")
        }
    }) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(state.groups, key = { it.id }) { group ->
                    val tagCount = state.tags.count { it.groupId == group.id }
                    ListItem(
                        headlineContent = { Text(group.name, fontWeight = FontWeight.Medium) },
                        supportingContent = { Text("$tagCount tags") },
                        trailingContent = {
                            IconButton(onClick = { groupToDelete = group }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                }
                if (state.groups.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            Text("No tag groups", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false; newName = "" },
            title = { Text("New Tag Group") },
            text = { OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Group Name") }, singleLine = true) },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) { vm.addGroup(newName); newName = ""; showAddDialog = false }
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false; newName = "" }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    groupToDelete?.let { group ->
        AlertDialog(
            onDismissRequest = { groupToDelete = null },
            title = { Text("Delete Group") },
            text = { Text("Delete \"${group.name}\" and all its tags?") },
            confirmButton = {
                TextButton(onClick = { vm.deleteGroup(group); groupToDelete = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { groupToDelete = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}
