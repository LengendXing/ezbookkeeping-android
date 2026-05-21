package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class TagItem(val id: Int, val name: String, val groupId: Int, val groupName: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTagSelectionSheet(
    visible: Boolean,
    title: String = "Select Tags",
    tags: List<TagItem> = emptyList(),
    selectedTagIds: List<Int> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (List<Int>) -> Unit
) {
    var selectedIds by remember(visible) { mutableStateOf(selectedTagIds.toMutableList()) }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                val grouped = tags.groupBy { it.groupName }
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    grouped.forEach { (group, groupTags) ->
                        if (group.isNotEmpty()) {
                            item(key = "header_$group") {
                                Text(
                                    group,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        items(groupTags, key = { it.id }) { tag ->
                            val selected = tag.id in selectedIds
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedIds = if (selected) {
                                            (selectedIds as MutableList).apply { remove(tag.id) }
                                        } else {
                                            (selectedIds as MutableList).apply { add(tag.id) }
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = {
                                        selectedIds = if (it) {
                                            (selectedIds as MutableList).apply { add(tag.id) }
                                        } else {
                                            (selectedIds as MutableList).apply { remove(tag.id) }
                                        }
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("#${tag.name}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    if (tags.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No tags available", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { selectedIds = mutableListOf() },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) { Text("Clear All") }
                    Button(
                        onClick = { onConfirm(selectedIds.toList()); onDismiss() },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) { Text("Confirm (${selectedIds.size})") }
                }
            }
        }
    }
}
