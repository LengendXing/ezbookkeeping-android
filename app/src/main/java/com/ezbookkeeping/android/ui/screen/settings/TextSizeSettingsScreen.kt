package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSizeSettingsScreen(navController: NavController) {
    val vm: TextSizeSettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Text Size") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Text Size Scale", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("${(state.textSizeScale * 100).toInt()}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Slider(value = state.textSizeScale, onValueChange = vm::setTextScale, valueRange = 0.8f..1.5f, steps = 6)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("80%", style = MaterialTheme.typography.labelSmall)
                        Text("150%", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Preview", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(state.previewText, fontSize = (14 * state.textSizeScale).sp)
                    Text("Income: ¥5,000.00", fontSize = (16 * state.textSizeScale).sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text("Expense: ¥2,350.00", fontSize = (16 * state.textSizeScale).sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
