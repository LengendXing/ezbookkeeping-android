package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AmountFilterUiState(
    val minAmount: String = "",
    val maxAmount: String = "",
    val error: String? = null
)

@HiltViewModel
class AmountFilterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AmountFilterUiState())
    val uiState: StateFlow<AmountFilterUiState> = _uiState.asStateFlow()

    fun onMinAmountChange(v: String) { _uiState.update { it.copy(minAmount = v, error = null) } }
    fun onMaxAmountChange(v: String) { _uiState.update { it.copy(maxAmount = v, error = null) } }

    fun validate(): Pair<Double?, Double?>? {
        val s = _uiState.value
        val min = s.minAmount.toDoubleOrNull()
        val max = s.maxAmount.toDoubleOrNull()
        if (s.minAmount.isNotBlank() && min == null) {
            _uiState.update { it.copy(error = "Invalid minimum amount") }; return null
        }
        if (s.maxAmount.isNotBlank() && max == null) {
            _uiState.update { it.copy(error = "Invalid maximum amount") }; return null
        }
        if (min != null && max != null && min > max) {
            _uiState.update { it.copy(error = "Minimum cannot exceed maximum") }; return null
        }
        return Pair(min, max)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountFilterScreen(navController: NavController) {
    val vm: AmountFilterViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Amount Filter") },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Cancel") } }
        )
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Filter transactions by amount range", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value = state.minAmount,
                onValueChange = vm::onMinAmountChange,
                label = { Text("Minimum Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.maxAmount,
                onValueChange = vm::onMaxAmountChange,
                label = { Text("Maximum Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Quick presets
            Text("Quick Presets", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { vm.onMinAmountChange("0"); vm.onMaxAmountChange("100") }, modifier = Modifier.weight(1f)) { Text("< 100") }
                OutlinedButton(onClick = { vm.onMinAmountChange("100"); vm.onMaxAmountChange("1000") }, modifier = Modifier.weight(1f)) { Text("100-1k") }
                OutlinedButton(onClick = { vm.onMinAmountChange("1000"); vm.onMaxAmountChange("") }, modifier = Modifier.weight(1f)) { Text("> 1k") }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val result = vm.validate()
                    if (result != null) {
                        navController.previousBackStackEntry?.savedStateHandle?.set("amountFilterMin", result.first)
                        navController.previousBackStackEntry?.savedStateHandle?.set("amountFilterMax", result.second)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Apply Filter") }
        }
    }
}
