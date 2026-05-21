package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun AccountFilterSettingsScreen(navController: NavController) {
    val vm: AccountFilterSettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Account Filter") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Select accounts to display", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = vm::toggleSelectAll) {
                    Text(if (state.selectAll) "Deselect All" else "Select All")
                }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(state.availableAccounts) { account ->
                    ListItem(
                        headlineContent = { Text(account.name) },
                        trailingContent = { Checkbox(checked = account.isSelected, onCheckedChange = { vm.toggleAccount(account.id) }) }
                    )
                }
            }
        }
    }
}
