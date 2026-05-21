package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TreeNode(
    val id: Int,
    val name: String,
    val color: String = "",
    val parentId: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeViewSelectionSheet(
    visible: Boolean,
    title: String = "Select",
    nodes: List<TreeNode> = emptyList(),
    selectedId: Int? = null,
    onDismiss: () -> Unit,
    onSelect: (Int?) -> Unit
) {
    var searchQuery by remember(visible) { mutableStateOf("") }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))

                val filtered = if (searchQuery.isBlank()) nodes else {
                    val matchingIds = nodes.filter { it.name.contains(searchQuery, ignoreCase = true) }
                        .flatMap { node -> generateSequence(node) { n -> nodes.find { it.id == n.parentId } }.map { it.id }.toSet() }
                        .toSet() + nodes.filter { it.name.contains(searchQuery, ignoreCase = true) }.map { it.id }
                    nodes.filter { it.id in matchingIds }
                }
                val rootNodes = filtered.filter { it.parentId == null }

                val flatNodes = mutableListOf<TreeNode>()
                rootNodes.forEach { parent ->
                    flatNodes.add(parent)
                    filtered.filter { it.parentId == parent.id }.forEach { child -> flatNodes.add(child) }
                }

                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    items(flatNodes, key = { it.id }) { node ->
                        val indent = if (node.parentId == null) 0.dp else 24.dp
                        TreeNodeRow(node, selectedId, indent) {
                            onSelect(it)
                            onDismiss()
                        }
                    }
                    if (flatNodes.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No results", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { onSelect(null); onDismiss() }, modifier = Modifier.align(Alignment.End)) {
                    Text("Clear Selection")
                }
            }
        }
    }
}

@Composable
private fun TreeNodeRow(
    node: TreeNode,
    selectedId: Int?,
    indent: Dp,
    onClick: (Int) -> Unit
) {
    val selected = node.id == selectedId
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(node.id) }
            .padding(horizontal = 16.dp + indent, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (node.color.isNotEmpty()) {
            Surface(
                modifier = Modifier.size(16.dp),
                shape = MaterialTheme.shapes.extraSmall,
                color = try { Color(android.graphics.Color.parseColor(node.color)) }
                    catch (_: Exception) { MaterialTheme.colorScheme.primary }
            ) {}
            Spacer(Modifier.width(8.dp))
        }
        Text(node.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (selected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
