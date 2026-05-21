package com.ezbookkeeping.android.ui.screen.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsSettingsUiState(
    val defaultChartType: ChartType = ChartType.PIE,
    val defaultDataType: DataDataType = DataDataType.EXPENSE,
    val defaultAggregation: DateAggregation = DateAggregation.MONTH,
    val defaultSortMethod: SortMethod = SortMethod.AMOUNT_DESC,
    val showSubCategory: Boolean = true,
    val showOtherCategory: Boolean = true,
    val otherCategoryThreshold: String = "5",
    val isLoading: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class StatisticsSettingsViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatisticsSettingsUiState())
    val uiState: StateFlow<StatisticsSettingsUiState> = _uiState.asStateFlow()

    fun onChartTypeChange(t: ChartType) { _uiState.update { it.copy(defaultChartType = t) } }
    fun onDataTypeChange(t: DataDataType) { _uiState.update { it.copy(defaultDataType = t) } }
    fun onAggregationChange(a: DateAggregation) { _uiState.update { it.copy(defaultAggregation = a) } }
    fun onSortMethodChange(s: SortMethod) { _uiState.update { it.copy(defaultSortMethod = s) } }
    fun onShowSubCategoryChange(v: Boolean) { _uiState.update { it.copy(showSubCategory = v) } }
    fun onShowOtherCategoryChange(v: Boolean) { _uiState.update { it.copy(showOtherCategory = v) } }
    fun onOtherCategoryThresholdChange(v: String) { _uiState.update { it.copy(otherCategoryThreshold = v) } }

    fun save(navController: NavController) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(isLoading = false, saved = true) }
            navController.popBackStack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsSettingsScreen(navController: NavController) {
    val vm: StatisticsSettingsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Statistics Settings") },
            navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } }
        )
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Chart Type
            Text("Default Chart Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                ChartType.entries.forEach { type ->
                    FilterChip(
                        selected = state.defaultChartType == type,
                        onClick = { vm.onChartTypeChange(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Data Type
            Text("Default Data Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                DataDataType.entries.forEach { type ->
                    FilterChip(
                        selected = state.defaultDataType == type,
                        onClick = { vm.onDataTypeChange(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Date Aggregation
            Text("Date Aggregation", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                DateAggregation.entries.forEach { agg ->
                    FilterChip(
                        selected = state.defaultAggregation == agg,
                        onClick = { vm.onAggregationChange(agg) },
                        label = { Text("By ${agg.name.lowercase().replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Sort Method
            Text("Sort Method", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                SortMethod.entries.forEach { sort ->
                    FilterChip(
                        selected = state.defaultSortMethod == sort,
                        onClick = { vm.onSortMethodChange(sort) },
                        label = { Text(sort.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider()

            // Display options
            Text("Display Options", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Show Sub-categories", modifier = Modifier.weight(1f))
                Switch(checked = state.showSubCategory, onCheckedChange = vm::onShowSubCategoryChange)
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Show Other Category", modifier = Modifier.weight(1f))
                Switch(checked = state.showOtherCategory, onCheckedChange = vm::onShowOtherCategoryChange)
            }

            if (state.showOtherCategory) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Threshold (%)", modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        value = state.otherCategoryThreshold,
                        onValueChange = vm::onOtherCategoryThresholdChange,
                        modifier = Modifier.width(80.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { vm.save(navController) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !state.isLoading
            ) { Text(stringResource(R.string.save)) }
        }
    }
}
