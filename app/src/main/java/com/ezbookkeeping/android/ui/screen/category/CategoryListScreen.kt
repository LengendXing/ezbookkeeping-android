package com.ezbookkeeping.android.ui.screen.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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

data class CategoryListUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = false,
    val selectedType: CategoryType = CategoryType.EXPENSE
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(navController: NavController) {
    val vm: CategoryListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.categories)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.CATEGORY_EDIT) }) {
                Icon(Icons.Default.Add, contentDescription = "Add category")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryTypeChip("Expense", state.selectedType == CategoryType.EXPENSE) { vm.setType(CategoryType.EXPENSE) }
                CategoryTypeChip("Income", state.selectedType == CategoryType.INCOME) { vm.setType(CategoryType.INCOME) }
                CategoryTypeChip("Transfer", state.selectedType == CategoryType.TRANSFER) { vm.setType(CategoryType.TRANSFER) }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                val filtered = state.categories.filter { it.type == state.selectedType }
                val roots = filtered.filter { it.parentId == null }
                val childrenMap = filtered.filter { it.parentId != null }.groupBy { it.parentId }

                LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                    if (roots.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                                Text("No categories", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    roots.forEach { root ->
                        item(key = root.id) {
                            CategoryRow(
                                category = root,
                                indent = 0,
                                onClick = { navController.navigate(Routes.CATEGORY_EDIT + "/${root.id}") },
                                onLongPress = { categoryToDelete = root }
                            )
                        }
                        val children = childrenMap[root.id].orEmpty()
                        items(children, key = { it.id }) { child ->
                            CategoryRow(
                                category = child,
                                indent = 1,
                                onClick = { navController.navigate(Routes.CATEGORY_EDIT + "/${child.id}") },
                                onLongPress = { categoryToDelete = child }
                            )
                        }
                    }
                }
            }
        }
    }

    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Delete Category") },
            text = { Text("Are you sure you want to delete \"${categoryToDelete?.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    categoryToDelete?.let { vm.deleteCategory(it) }
                    categoryToDelete = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { categoryToDelete = null }) { Text(stringResource(R.string.cancel)) } }
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
            Text(
                category.name,
                fontWeight = if (indent == 0) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Box(
                Modifier.size(36.dp).clip(MaterialTheme.shapes.small).background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(category.icon, fontSize = 16.sp, color = Color.White)
            }
        },
        modifier = Modifier
            .padding(start = (indent * 32).dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongPress)
            .padding(horizontal = 8.dp)
    )
}

private fun parseColor(hex: String): Color {
    return try {
        val cleaned = hex.removePrefix("#")
        val colorVal = cleaned.toLong(16)
        when (cleaned.length) {
            6 -> Color(0xFF000000 or colorVal)
            8 -> Color(colorVal)
            else -> Color(0xFF6200EE)
        }
    } catch (_: Exception) {
        Color(0xFF6200EE)
    }
}
