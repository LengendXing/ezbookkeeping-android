package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.data.db.entity.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditScreen(navController: NavController, transactionId: Int? = null) {
    val vm: TransactionEditViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    LaunchedEffect(transactionId) { transactionId?.let { vm.loadTransaction(it) } }
    LaunchedEffect(state.saveSuccess) { if (state.saveSuccess) navController.popBackStack() }

    Scaffold(topBar = { TopAppBar(title = { Text(if (state.isEdit) "Edit Transaction" else "New Transaction") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Cancel") } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Type selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TransactionType.entries.forEach { type ->
                    FilterChip(selected = state.type == type, onClick = { vm.onTypeChange(type) }, label = { Text(type.name) })
                }
            }
            OutlinedTextField(value = state.sourceAmount, onValueChange = vm::onAmountChange, label = { Text("Amount") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = state.comment, onValueChange = vm::onCommentChange, label = { Text("Comment") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = state.date, onValueChange = vm::onDateChange, label = { Text("Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.weight(1f))
            Button(onClick = vm::save, modifier = Modifier.fillMaxWidth().height(48.dp), enabled = !state.isLoading) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary) else Text(if (state.isEdit) "Update" else "Save")
            }
        }
    }
}
