package com.ezbookkeeping.android.ui.screen.tag

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.ezbookkeeping.android.data.db.entity.TagEntity
import com.ezbookkeeping.android.data.db.entity.TagGroupEntity
import com.ezbookkeeping.android.ui.navigation.Routes

data class TagListUiState(
    val groups: List<TagGroupEntity> = emptyList(),
    val tags: List<TagEntity> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TagListScreen(navController: NavController) {
    val vm: TagListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var tagToDelete by remember { mutableStateOf<TagEntity?>(null) }
    var groupToDelete by remember { mutableStateOf<TagGroupEntity?>(null) }
    var showAddGroupDialog by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tags)) },
                actions = {
                    IconButton(onClick = { showAddGroupDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add group")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.TAG_EDIT) }) {
                Icon(Icons.Default.Add, contentDescription = "Add tag")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (state.groups.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No tag groups yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { showAddGroupDialog = true }) { Text("Create Tag Group") }
                }
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                state.groups.forEach { group ->
                    val groupTags = state.tags.filter { it.groupId == group.id }

                    item(key = "header_${group.id}") {
                        Row(
                            Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(group.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = { groupToDelete = group }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete group", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    item(key = "flow_${group.id}") {
                        if (groupTags.isEmpty()) {
                            Text("No tags in this group", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                        } else {
                            FlowRow(modifier = Modifier.padding(bottom = 8.dp)) {
                                groupTags.forEach { tag ->
                                    TagChip(
                                        tag = tag,
                                        onEdit = { navController.navigate(Routes.TAG_EDIT) },
                                        onDelete = { tagToDelete = tag }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (tagToDelete != null) {
        AlertDialog(
            onDismissRequest = { tagToDelete = null },
            title = { Text("Delete Tag") },
            text = { Text("Are you sure you want to delete \"${tagToDelete?.name}\"?") },
            confirmButton = {
                TextButton(onClick = { tagToDelete?.let { vm.deleteTag(it) }; tagToDelete = null })
                { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { tagToDelete = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    if (groupToDelete != null) {
        AlertDialog(
            onDismissRequest = { groupToDelete = null },
            title = { Text("Delete Tag Group") },
            text = { Text("Delete \"${groupToDelete?.name}\" and all its tags?") },
            confirmButton = {
                TextButton(onClick = { groupToDelete?.let { vm.deleteGroup(it) }; groupToDelete = null })
                { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { groupToDelete = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    if (showAddGroupDialog) {
        AlertDialog(
            onDismissRequest = { showAddGroupDialog = false; newGroupName = "" },
            title = { Text("New Tag Group") },
            text = { OutlinedTextField(value = newGroupName, onValueChange = { newGroupName = it }, label = { Text("Group Name") }, singleLine = true) },
            confirmButton = {
                TextButton(onClick = {
                    if (newGroupName.isNotBlank()) {
                        vm.addGroup(newGroupName)
                        newGroupName = ""
                        showAddGroupDialog = false
                    }
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = { TextButton(onClick = { showAddGroupDialog = false; newGroupName = "" }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TagChip(tag: TagEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        InputChip(
            selected = false,
            onClick = onEdit,
            label = { Text(tag.name) },
            trailingIcon = {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(16.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                }
            },
            modifier = Modifier.padding(2.dp)
        )
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(text = { Text("Edit") }, onClick = { showMenu = false; onEdit() })
            DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() })
        }
    }
}
