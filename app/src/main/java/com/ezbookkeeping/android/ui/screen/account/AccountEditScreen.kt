package com.ezbookkeeping.android.ui.screen.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    Scaffold(topBar = { TopAppBar(title = { Text(if (state.isEdit) stringResource(R.string.edit_account) else stringResource(R.string.new_account)) }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { AccountType.entries.forEach { type -> FilterChip(selected = state.type == type, onClick = { vm.onTypeChange(type) }, label = { Text(type.name) }) } }
            OutlinedTextField(value = state.name, onValueChange = vm::onNameChange, label = { Text(stringResource(R.string.account_name)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = state.currency, onValueChange = vm::onCurrencyChange, label = { Text(stringResource(R.string.currency)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = state.initialBalance, onValueChange = vm::onInitialBalanceChange, label = { Text(stringResource(R.string.initial_balance)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true)
            if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.weight(1f))
            Button(onClick = vm::save, modifier = Modifier.fillMaxWidth().height(48.dp), enabled = !state.isLoading) { if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary) else Text(if (state.isEdit) stringResource(R.string.update) else stringResource(R.string.save)) }
        }
    }
}
