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
fun SessionListScreen(navController: NavController) {
    val vm: SessionListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Device & Sessions") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } }) }) { padding ->
        if (state.sessions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No active sessions", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Current Device", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                state.sessions.filter { it.isCurrent }.forEach { session -> SessionItem(session) { vm.revokeSession(session.id) } }
                if (state.sessions.any { !it.isCurrent }) {
                    Spacer(Modifier.height(8.dp))
                    Text("Other Devices", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    state.sessions.filter { !it.isCurrent }.forEach { session -> SessionItem(session) { vm.revokeSession(session.id) } }
                }
                Spacer(Modifier.weight(1f))
                OutlinedButton(onClick = vm::revokeAllOtherSessions, modifier = Modifier.fillMaxWidth()) { Text("Revoke All Other Sessions") }
            }
        }
    }
}

@Composable
private fun SessionItem(session: SessionInfo, onRevoke: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(session.deviceName, style = MaterialTheme.typography.bodyLarge); Text(session.lastActive, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (!session.isCurrent) OutlinedButton(onClick = onRevoke) { Text("Revoke") }
            else Text("Current", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

data class SessionInfo(val id: String, val deviceName: String, val lastActive: String, val isCurrent: Boolean)
