package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
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
fun CloudSyncSettingsScreen(navController: NavController) {
    val vm: CloudSyncSettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Cloud Sync") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Sync Provider", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Column(Modifier.selectableGroup()) {
                        SyncProvider.entries.forEach { provider ->
                            Row(
                                Modifier.selectable(selected = state.syncProvider == provider, onClick = { vm.setProvider(provider) }, role = Role.RadioButton)
                                    .fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = state.syncProvider == provider, onClick = null)
                                Text(provider.label, modifier = Modifier.padding(start = 12.dp))
                            }
                        }
                    }
                }
            }

            if (state.syncProvider != SyncProvider.NONE) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sync Options", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        ListItem(headlineContent = { Text("Enable Cloud Sync") }, trailingContent = { Switch(checked = state.isSyncEnabled, onCheckedChange = vm::setSyncEnabled) })
                        ListItem(headlineContent = { Text("Auto Sync") }, trailingContent = { Switch(checked = state.autoSync, onCheckedChange = vm::setAutoSync) })
                        ListItem(headlineContent = { Text("Wi-Fi Only") }, trailingContent = { Switch(checked = state.wifiOnly, onCheckedChange = vm::setWifiOnly) })
                    }
                }

                Button(onClick = vm::syncNow, modifier = Modifier.fillMaxWidth(), enabled = !state.isSyncing) {
                    if (state.isSyncing) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Sync Now")
                }
            }

            if (state.lastSyncTime != null) {
                Text("Last sync: ${state.lastSyncTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (state.syncResult != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(state.syncResult!!, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}
