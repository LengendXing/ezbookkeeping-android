package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val vm: SettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(state.isLoggedOut) { if (state.isLoggedOut) navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.settings)) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = state.serverUrl, onValueChange = vm::onServerUrlChange, label = { Text(stringResource(R.string.server_url)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Button(onClick = vm::saveServerUrl, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.save_server_url)) }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.data), style = MaterialTheme.typography.titleMedium)
            ListItem(headlineContent = { Text(stringResource(R.string.accounts)) }, modifier = Modifier.clickable { navController.navigate(Routes.ACCOUNT_LIST) })
            ListItem(headlineContent = { Text(stringResource(R.string.categories)) }, modifier = Modifier.clickable { navController.navigate(Routes.CATEGORY_LIST) })
            ListItem(headlineContent = { Text(stringResource(R.string.tags)) }, modifier = Modifier.clickable { navController.navigate(Routes.TAG_LIST) })
            ListItem(headlineContent = { Text(stringResource(R.string.templates)) }, modifier = Modifier.clickable { navController.navigate(Routes.TEMPLATE_LIST) })
            ListItem(headlineContent = { Text(stringResource(R.string.exchange_rates)) }, modifier = Modifier.clickable { navController.navigate(Routes.EXCHANGE) })
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text(stringResource(R.string.about), style = MaterialTheme.typography.titleMedium)
            ListItem(headlineContent = { Text(stringResource(R.string.about_ez_bookkeeping)) }, modifier = Modifier.clickable { navController.navigate(Routes.ABOUT) })
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Button(onClick = vm::logout, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text(stringResource(R.string.sign_out)) }
        }
    }
}
