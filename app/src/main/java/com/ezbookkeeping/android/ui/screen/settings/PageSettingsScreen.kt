package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun PageSettingsScreen(navController: NavController) {
    val vm: PageSettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Page Settings") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Default Landing Page", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    state.availablePages.forEach { page ->
                        Row(Modifier.fillMaxWidth()) {
                            RadioButton(selected = state.defaultLandingPage == page, onClick = { vm.setLandingPage(page) })
                            Text(page, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Display Options", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    ListItem(headlineContent = { Text("Show Overview Card") }, trailingContent = { Switch(checked = state.showOverviewCard, onCheckedChange = vm::toggleOverviewCard) })
                    ListItem(headlineContent = { Text("Show Transaction Amount") }, trailingContent = { Switch(checked = state.showTransactionAmount, onCheckedChange = vm::toggleTransactionAmount) })
                    ListItem(headlineContent = { Text("Show Transaction Comment") }, trailingContent = { Switch(checked = state.showTransactionComment, onCheckedChange = vm::toggleTransactionComment) })
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Transaction Page Size", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(20, 50, 100).forEach { size ->
                            FilterChip(selected = state.transactionPageSize == size, onClick = { vm.setPageSize(size) }, label = { Text("$size") })
                        }
                    }
                }
            }
        }
    }
}
