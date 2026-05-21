package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPresetScreen(navController: NavController) {
    val vm: CategoryPresetViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Category Presets") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Select preset categories to import", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.presets) { preset ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = preset.id in state.selectedPresets, onCheckedChange = { vm.togglePreset(preset.id) })
                                Column(Modifier.weight(1f).padding(start = 8.dp)) {
                                    Text(preset.name, fontWeight = FontWeight.SemiBold)
                                    Text("${preset.type} · ${preset.subCategories.size} sub-categories", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(preset.subCategories.joinToString(", "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, maxLines = 2)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = vm::importPresets,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.selectedPresets.isNotEmpty() && !state.isImporting
                ) {
                    if (state.isImporting) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Import ${state.selectedPresets.size} Preset(s)")
                    }
                }
            }

            if (state.importResult != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(state.importResult!!, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}
