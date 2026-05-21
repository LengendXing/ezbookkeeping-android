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
fun DisplayOrderSettingsScreen(navController: NavController) {
    val vm: DisplayOrderSettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Display Order") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DisplayTab.entries.forEach { tab ->
                    FilterChip(selected = state.activeTab == tab, onClick = { vm.setTab(tab) }, label = { Text(tab.label) })
                }
            }

            val list = if (state.activeTab == DisplayTab.ACCOUNTS) state.accountOrder else state.categoryOrder
            Text("Drag to reorder (use arrows)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(list, key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.name, fontWeight = FontWeight.Medium) },
                        leadingContent = { Text("#${item.order}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                        trailingContent = {
                            Row {
                                OutlinedButton(onClick = { vm.moveUp(item.id) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(36.dp)) {
                                    Text("↑")
                                }
                                Spacer(Modifier.width(4.dp))
                                OutlinedButton(onClick = { vm.moveDown(item.id) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(36.dp)) {
                                    Text("↓")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
