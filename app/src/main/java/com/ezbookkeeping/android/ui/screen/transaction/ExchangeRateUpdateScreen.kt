package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateUpdateScreen(navController: NavController) {
    val vm: ExchangeRateUpdateViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Exchange Rate Update") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Auto Update", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        ListItem(
                            headlineContent = { Text("Enable Auto Update") },
                            trailingContent = { Switch(checked = state.isAutoUpdate, onCheckedChange = vm::setAutoUpdate) }
                        )
                        if (state.isAutoUpdate) {
                            Text("Update Frequency", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                UpdateFrequency.entries.forEach { freq ->
                                    FilterChip(selected = state.updateFrequency == freq, onClick = { vm.setFrequency(freq) }, label = { Text(freq.label) })
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Rate Source", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Column(Modifier.selectableGroup()) {
                            state.availableSources.forEach { source ->
                                Row(
                                    Modifier.selectable(selected = state.selectedSource == source.id, onClick = { vm.setSource(source.id) }, role = Role.RadioButton)
                                        .fillMaxWidth().padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = state.selectedSource == source.id, onClick = null)
                                    Text(source.name, modifier = Modifier.padding(start = 12.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(onClick = vm::updateNow, modifier = Modifier.fillMaxWidth(), enabled = !state.isUpdating) {
                    if (state.isUpdating) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Update Now")
                    }
                }
            }

            if (state.lastUpdated != null) {
                item {
                    Text("Last updated: ${state.lastUpdated}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (state.updateResult != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Text(state.updateResult!!, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
    }
}
