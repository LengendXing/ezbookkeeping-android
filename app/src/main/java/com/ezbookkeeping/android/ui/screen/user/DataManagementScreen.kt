package com.ezbookkeeping.android.ui.screen.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagementScreen(navController: NavController) {
    val vm: DataManagementViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Data Management") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Export
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Export Data", style = MaterialTheme.typography.titleMedium)
                    Text("Export all your data as a file for backup.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = vm::exportData, modifier = Modifier.fillMaxWidth()) { Text("Export") }
                }
            }
            // Import
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Import Data", style = MaterialTheme.typography.titleMedium)
                    Text("Import data from a backup file.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { /* TODO: file picker */ }, modifier = Modifier.fillMaxWidth()) { Text("Import") }
                }
            }
            // Clear data
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Clear Local Data", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    Text("Remove all local data. This cannot be undone.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = vm::clearData, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Clear Data") }
                }
            }
        }
    }
}
