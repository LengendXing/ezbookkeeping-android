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
fun TwoFactorAuthScreen(navController: NavController) {
    val vm: TwoFactorAuthViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Two-Factor Authentication") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (state.isEnabled) {
                Text("2FA is enabled", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text("Your account is protected with two-factor authentication.", style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = vm::disable2fa, modifier = Modifier.fillMaxWidth()) { Text("Disable 2FA") }
            } else {
                Text("2FA is not enabled", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Enable two-factor authentication for extra security.", style = MaterialTheme.typography.bodyMedium)
                Button(onClick = vm::enable2fa, modifier = Modifier.fillMaxWidth()) { Text("Enable 2FA") }
            }
            if (state.backupCodes.isNotEmpty()) {
                HorizontalDivider()
                Text("Backup Codes", style = MaterialTheme.typography.titleSmall)
                state.backupCodes.forEach { code -> Text(code, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp)) }
                Text("Save these codes in a safe place.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
