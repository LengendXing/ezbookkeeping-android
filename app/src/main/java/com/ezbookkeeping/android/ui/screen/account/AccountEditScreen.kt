package com.ezbookkeeping.android.ui.screen.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditScreen(navController: NavController, accountId: Int? = null) {
    val vm: AccountEditViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    LaunchedEffect(accountId) { accountId?.let { vm.loadAccount(it) } }
    LaunchedEffect(state.saveSuccess) { if (state.saveSuccess) navController.popBackStack() }

    var showColorPicker by remember { mutableStateOf(false) }

    val accountColors = listOf("#1B6B4D", "#2196F3", "#E91E63", "#FF9800", "#9C27B0", "#00BCD4", "#4CAF50", "#F44336", "#795548", "#607D8B", "#FF5722", "#3F51B5")

    Scaffold(topBar = {
        TopAppBar(title = { Text(if (state.isEdit) stringResource(R.string.edit_account) else stringResource(R.string.new_account)) },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Type selector
            item { Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                AccountType.entries.forEach { type -> FilterChip(selected = state.type == type, onClick = { vm.onTypeChange(type) }, label = { Text(type.name.replace("_", " ")) }, modifier = Modifier.weight(1f)) }
            } }
            // Name
            item { OutlinedTextField(value = state.name, onValueChange = vm::onNameChange, label = { Text(stringResource(R.string.account_name)) }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            // Color selector
            item {
                Column { Text("Color", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        accountColors.forEach { colorHex ->
                            val selected = state.color.equals(colorHex, ignoreCase = true)
                            Surface(onClick = { vm.onColorChange(colorHex) }, modifier = Modifier.size(32.dp), shape = MaterialTheme.shapes.small,
                                color = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (_: Exception) { MaterialTheme.colorScheme.primary },
                                border = if (selected) ButtonDefaults.outlinedButtonBorder else null) {}
                        }
                    }
                }
            }
            // Currency
            item { OutlinedTextField(value = state.currency, onValueChange = vm::onCurrencyChange, label = { Text(stringResource(R.string.currency)) }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
            // Initial balance
            item { OutlinedTextField(value = state.initialBalance, onValueChange = vm::onInitialBalanceChange, label = { Text(stringResource(R.string.initial_balance)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true) }
            // Balance (for editing existing)
            if (state.isEdit) {
                item { OutlinedTextField(value = state.balance, onValueChange = vm::onBalanceChange, label = { Text("Balance") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true) }
            }
            // Credit limit (for credit card accounts)
            if (state.type == AccountType.LIABILITY) {
                item { OutlinedTextField(value = state.creditLimit, onValueChange = vm::onCreditLimitChange, label = { Text("Credit Limit") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true) }
            }
            // Error
            if (state.error != null) item { Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            // Save
            item { Button(onClick = vm::save, modifier = Modifier.fillMaxWidth().height(48.dp), enabled = !state.isLoading) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                else Text(if (state.isEdit) stringResource(R.string.update) else stringResource(R.string.save))
            } }
        }
    }
}
