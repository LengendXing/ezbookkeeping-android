package com.ezbookkeeping.android.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.LockType

data class ApplicationLockUiState(
    val currentLockType: LockType = LockType.NONE,
    val isSettingPin: Boolean = false,
    val isSettingPassword: Boolean = false,
    val inputCode: String = "",
    val confirmCode: String = "",
    val error: String? = null,
    val isBiometricAvailable: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationLockScreen(navController: NavController) {
    val vm: ApplicationLockViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Application Lock") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Lock Method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)

            LockTypeOption("None", "No lock protection", state.currentLockType == LockType.NONE) { vm.setLockType(LockType.NONE) }
            LockTypeOption("PIN", "4-digit PIN code", state.currentLockType == LockType.PIN) { vm.startSettingPin() }
            LockTypeOption("Password", "Alphanumeric password", state.currentLockType == LockType.PASSWORD) { vm.startSettingPassword() }
            if (state.isBiometricAvailable) {
                LockTypeOption("Biometric", "Fingerprint or face recognition", state.currentLockType == LockType.BIOMETRIC) { vm.setLockType(LockType.BIOMETRIC) }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            if (state.isSettingPin) {
                PinSetupSection(state, vm)
            } else if (state.isSettingPassword) {
                PasswordSetupSection(state, vm)
            }

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.weight(1f))

            if (state.currentLockType != LockType.NONE) {
                OutlinedButton(onClick = vm::removeLock, modifier = Modifier.fillMaxWidth()) {
                    Text("Remove Lock", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun LockTypeOption(title: String, subtitle: String, selected: Boolean, onSelect: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onSelect)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PinSetupSection(state: ApplicationLockUiState, vm: ApplicationLockViewModel) {
    Text("Set PIN Code", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    if (state.confirmCode.isEmpty()) {
        OutlinedTextField(
            value = state.inputCode,
            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) vm.onInputCodeChange(it) },
            label = { Text("Enter 4-digit PIN") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        if (state.inputCode.length == 4) {
            Button(onClick = vm::confirmFirstPin, modifier = Modifier.fillMaxWidth()) { Text("Confirm") }
        }
    } else {
        OutlinedTextField(
            value = state.inputCode,
            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) vm.onInputCodeChange(it) },
            label = { Text("Confirm PIN") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        if (state.inputCode.length == 4) {
            Button(onClick = vm::confirmPin, modifier = Modifier.fillMaxWidth()) { Text("Save PIN") }
        }
    }
}

@Composable
private fun PasswordSetupSection(state: ApplicationLockUiState, vm: ApplicationLockViewModel) {
    Text("Set Password", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    if (state.confirmCode.isEmpty()) {
        OutlinedTextField(
            value = state.inputCode,
            onValueChange = vm::onInputCodeChange,
            label = { Text("Enter password (min 4 chars)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        if (state.inputCode.length >= 4) {
            Button(onClick = vm::confirmFirstPassword, modifier = Modifier.fillMaxWidth()) { Text("Confirm") }
        }
    } else {
        OutlinedTextField(
            value = state.inputCode,
            onValueChange = vm::onInputCodeChange,
            label = { Text("Confirm password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        if (state.inputCode.length >= 4) {
            Button(onClick = vm::confirmPassword, modifier = Modifier.fillMaxWidth()) { Text("Save Password") }
        }
    }
}
