package com.ezbookkeeping.android.ui.screen.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.CategoryEntity
import com.ezbookkeeping.android.data.db.entity.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(navController: NavController, categoryId: Int? = null) {
    val vm: CategoryEditViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    LaunchedEffect(categoryId) { categoryId?.let { vm.loadCategory(it) } }
    LaunchedEffect(state.saveSuccess) { if (state.saveSuccess) navController.popBackStack() }
    var showParentPicker by remember { mutableStateOf(false) }

    val categoryColors = listOf("#F44336", "#E91E63", "#9C27B0", "#2196F3", "#00BCD4", "#4CAF50", "#FF9800", "#795548", "#607D8B", "#3F51B5")

    Scaffold(topBar = {
        TopAppBar(title = { Text(if (state.isEdit) stringResource(R.string.edit_category) else stringResource(R.string.new_category)) },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Type selector
            item { Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                CategoryType.entries.forEach { type -> FilterChip(selected = state.type == type, onClick = { vm.onTypeChange(type) }, label = { Text(type.name) }, modifier = Modifier.weight(1f)) }
            } }
            // Name
            item { OutlinedTextField(value = state.name, onValueChange = vm::onNameChange, label = { Text(stringResource(R.string.category_name)) }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            // Color selector
            item { Column { Text("Color", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    categoryColors.forEach { colorHex ->
                        val selected = state.color.equals(colorHex, ignoreCase = true)
                        Surface(onClick = { vm.onColorChange(colorHex) }, modifier = Modifier.size(32.dp), shape = MaterialTheme.shapes.small,
                            color = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary },
                            border = if (selected) ButtonDefaults.outlinedButtonBorder else null) {}
                    }
                }
            } }
            // Parent category picker
            item { ListItem(headlineContent = { Text("Parent Category") },
                supportingContent = { Text(state.parentCategories.find { it.id == state.parentId }?.name ?: "None (Top-level)") },
                modifier = Modifier.clickable { showParentPicker = true }) }
            // Error
            if (state.error != null) item { Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            // Save
            item { Button(onClick = vm::save, modifier = Modifier.fillMaxWidth().height(48.dp), enabled = !state.isLoading) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text(if (state.isEdit) stringResource(R.string.update) else stringResource(R.string.save))
            } }
        }
    }

    // Parent category picker
    if (showParentPicker) {
        AlertDialog(onDismissRequest = { showParentPicker = false }, title = { Text("Parent Category") },
            text = { LazyColumn {
                item { ListItem(headlineContent = { Text("None (Top-level)") }, modifier = Modifier.clickable { vm.onParentChange(null); showParentPicker = false }) }
                items(state.parentCategories) { cat ->
                    ListItem(headlineContent = { Text(cat.name) }, trailingContent = { if (cat.id == state.parentId) Text("✓") },
                        modifier = Modifier.clickable { vm.onParentChange(cat.id); showParentPicker = false })
                }
            } },
            confirmButton = { TextButton(onClick = { showParentPicker = false }) { Text(stringResource(R.string.cancel)) } })
    }
}
