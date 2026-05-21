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

data class SelectableItem(
    val id: Int,
    val name: String,
    val subtitle: String = "",
    val color: String = "",
    val group: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoColumnListItemSelectionSheet(
    visible: Boolean,
    title: String = "Select",
    items: List<SelectableItem> = emptyList(),
    selectedId: Int? = null,
    groupBy: (SelectableItem) -> String = { it.group },
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                val grouped = items.groupBy(groupBy)
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    grouped.forEach { (group, groupItems) ->
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
                        items(groupItems, key = { it.id }) { item ->
                            val selected = item.id == selectedId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(item.id); onDismiss() }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item.color.isNotEmpty()) {
                                    Surface(
                                        modifier = Modifier.size(20.dp),
                                        shape = MaterialTheme.shapes.extraSmall,
                                        color = try { Color(android.graphics.Color.parseColor(item.color)) }
                                            catch (_: Exception) { MaterialTheme.colorScheme.primary }
                                    ) {}
                                    Spacer(Modifier.width(8.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, style = MaterialTheme.typography.bodyLarge)
                                    if (item.subtitle.isNotEmpty()) {
                                        Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                if (selected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
