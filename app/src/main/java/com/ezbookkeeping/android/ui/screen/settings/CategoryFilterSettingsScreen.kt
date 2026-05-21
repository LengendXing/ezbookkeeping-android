package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterSettingsScreen(navController: NavController) {
    val vm: CategoryFilterSettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Category Filter") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Select categories to display", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = vm::toggleSelectAll) { Text(if (state.selectAll) "Deselect All" else "Select All") }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                item { Text("Expense", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error) }
                items(state.expenseCategories) { cat ->
                    ListItem(headlineContent = { Text(cat.name) }, trailingContent = { Checkbox(checked = cat.isSelected, onCheckedChange = { vm.toggleCategory(cat.id) }) })
                }
                item { Spacer(Modifier.height(8.dp)) }
                item { Text("Income", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary) }
                items(state.incomeCategories) { cat ->
                    ListItem(headlineContent = { Text(cat.name) }, trailingContent = { Checkbox(checked = cat.isSelected, onCheckedChange = { vm.toggleCategory(cat.id) }) })
                }
            }
        }
    }
}
