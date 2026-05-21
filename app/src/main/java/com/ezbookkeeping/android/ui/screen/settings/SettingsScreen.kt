package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.foundation.clickable
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
import com.ezbookkeeping.android.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val vm: SettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    LaunchedEffect(state.isLoggedOut) { if (state.isLoggedOut) navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.settings)) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            SectionHeader("User")
            SettingsItem("User Profile") { /* TODO */ }
            SettingsItem(stringResource(R.string.categories)) { navController.navigate(Routes.CATEGORY_LIST) }
            SettingsItem(stringResource(R.string.tags)) { navController.navigate(Routes.TAG_LIST) }
            SettingsItem(stringResource(R.string.templates)) { navController.navigate(Routes.TEMPLATE_LIST) }
            SettingsItem("Data Management") { /* TODO */ }
            SettingsItem("Two-Factor Authentication") { /* TODO */ }
            SettingsItem("Device & Sessions") { /* TODO */ }
            SettingsItem(stringResource(R.string.sign_out), isDestructive = true, onClick = vm::logout)

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SectionHeader("Application")
            SettingsToggleItem("Dark Theme", state.isDarkTheme, vm::onDarkThemeToggle)
            SettingsItem("Timezone", subtitle = state.timezone) { /* TODO */ }
            SettingsItem("Application Lock", subtitle = if (state.isAppLockEnabled) "Enabled" else "Disabled") { navController.navigate(Routes.UNLOCK) }
            SettingsItem(stringResource(R.string.exchange_rates), subtitle = state.exchangeRateLastUpdate) { navController.navigate(Routes.EXCHANGE) }
            SettingsToggleItem("Auto-update Exchange Rates", state.isAutoUpdateExchangeRates, vm::onAutoUpdateExchangeRatesToggle)
            SettingsToggleItem("Show Account Balance", state.showAccountBalance, vm::onShowAccountBalanceToggle)
            SettingsToggleItem("Enable Animation", state.isAnimationEnabled, vm::onAnimationToggle)

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            if (!state.isStandalone) {
                SectionHeader("Server")
                OutlinedTextField(value = state.serverUrl, onValueChange = vm::onServerUrlChange, label = { Text(stringResource(R.string.server_url)) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), singleLine = true)
                Spacer(Modifier.height(4.dp))
                Button(onClick = vm::saveServerUrl, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) { Text(stringResource(R.string.save_server_url)) }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
            }
            SectionHeader("About")
            SettingsItem(stringResource(R.string.about_ez_bookkeeping), subtitle = "v${state.version}") { navController.navigate(Routes.ABOUT) }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
}

@Composable
private fun SettingsItem(title: String, subtitle: String? = null, isDestructive: Boolean = false, onClick: () -> Unit) {
    ListItem(headlineContent = { Text(title, color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface) },
        supportingContent = subtitle?.let { { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        modifier = Modifier.clickable(onClick = onClick))
}

@Composable
private fun SettingsToggleItem(title: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    ListItem(headlineContent = { Text(title) }, trailingContent = { Switch(checked = checked, onCheckedChange = onToggle) })
}
