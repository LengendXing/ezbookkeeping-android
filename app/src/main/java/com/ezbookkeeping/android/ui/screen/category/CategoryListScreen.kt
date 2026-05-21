package com.ezbookkeeping.android.ui.screen.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.CategoryEntity
import com.ezbookkeeping.android.data.db.entity.CategoryType
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.parseColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryListScreen(navController: NavController) {
    val vm: CategoryListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var categoryActions by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.categories)) },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.CATEGORY_PRESET) }) {
                        Icon(Icons.Default.Visibility, contentDescription = "Presets", modifier = Modifier.size(20.dp))
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.CATEGORY_EDIT) }) {
                Icon(Icons.Default.Add, contentDescription = "Add category")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryType.entries.forEach { type ->
                    FilterChip(selected = state.selectedType == type, onClick = { vm.setType(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() } , style = MaterialTheme.typography.labelSmall) })
                }
            }
            // Show hidden toggle
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Show hidden", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                Switch(checked = state.showHidden, onCheckedChange = { vm.toggleShowHidden() })
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                val filtered = state.categories.filter { it.type == state.selectedType && (state.showHidden || !it.isHidden) }
                val roots = filtered.filter { it.parentId == null }
                val childrenMap = filtered.filter { it.parentId != null }.groupBy { it.parentId }

                LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                    if (roots.isEmpty()) {
                        item { Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { Text("No categories", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                    }
                    roots.forEach { root ->
                        item(key = root.id) {
                            CategoryRow(root, 0,
                                onClick = { navController.navigate(Routes.CATEGORY_EDIT + "/${root.id}") },
                                onLongPress = { categoryActions = root })
                        }
                        childrenMap[root.id].orEmpty().forEach { child ->
                            item(key = child.id) {
                                CategoryRow(child, 1,
                                    onClick = { navController.navigate(Routes.CATEGORY_EDIT + "/${child.id}") },
                                    onLongPress = { categoryActions = child })
                            }
                        }
                    }
                }
            }
        }
    }

    // Category actions menu
    categoryActions?.let { cat ->
        AlertDialog(onDismissRequest = { categoryActions = null },
            title = { Text(cat.name) },
            text = {
                Column {
                    TextButton(onClick = { categoryActions = null; navController.navigate(Routes.CATEGORY_EDIT + "/${cat.id}") }) { Text("Edit") }
                    TextButton(onClick = { vm.toggleHidden(cat); categoryActions = null }) { Text(if (cat.isHidden) "Show" else "Hide") }
                    HorizontalDivider()
                    TextButton(onClick = { vm.deleteCategory(cat); categoryActions = null }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = { TextButton(onClick = { categoryActions = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun CategoryTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryRow(category: CategoryEntity, indent: Int, onClick: () -> Unit, onLongPress: () -> Unit) {
    val bgColor = parseColor(category.color)
    ListItem(
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(category.name, fontWeight = if (indent == 0) FontWeight.Medium else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (category.isHidden) { Spacer(Modifier.width(4.dp)); Text("(hidden)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        },
        leadingContent = { Box(Modifier.size(36.dp).clip(MaterialTheme.shapes.small).background(bgColor), contentAlignment = Alignment.Center) { Text(category.icon, fontSize = 16.sp, color = Color.White) } },
        modifier = Modifier.padding(start = (indent * 32).dp).combinedClickable(onClick = onClick, onLongClick = onLongPress).padding(horizontal = 8.dp)
    )
}

