package com.ezbookkeeping.android.ui.screen.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.ui.component.TrendsBarChart
import com.ezbookkeeping.android.ui.component.AccountBalanceTrendsBarChart

enum class InsightTab { OVERVIEW, QUERY, DATA_TABLE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightExplorerScreen(navController: NavController) {
    val vm: InsightExplorerViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(InsightTab.OVERVIEW) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Data Insights") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Tab selector
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InsightTab.entries.forEach { tab ->
                    FilterChip(selected = selectedTab == tab, onClick = { selectedTab = tab },
                        label = { Text(tab.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) })
                }
            }

            when (selectedTab) {
                InsightTab.OVERVIEW -> InsightOverviewTab(state, vm)
                InsightTab.QUERY -> InsightQueryTab(vm)
                InsightTab.DATA_TABLE -> InsightDataTableTab(state)
            }
        }
    }
}

@Composable
private fun InsightOverviewTab(state: InsightExplorerUiState, vm: InsightExplorerViewModel) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InsightPeriod.entries.forEach { period ->
                    FilterChip(selected = state.selectedPeriod == period, onClick = { vm.setPeriod(period) }, label = { Text(period.label) })
                }
            }
        }
        if (state.isLoading) {
            item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
        } else {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Savings Rate", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("${state.savingsRate}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            items(state.insights) { insight ->
                ListItem(headlineContent = { Text(insight.title) },
                    supportingContent = { Text(insight.trend, color = if (insight.isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) },
                    trailingContent = { Text(insight.value, fontWeight = FontWeight.Bold) })
            }
            item { Text("Expense Trend", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary) }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        TrendsBarChart(state.trendData.map { it.month to it.expense.toFloat() }, barColor = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightQueryTab(vm: InsightExplorerViewModel) {
    var queryType by remember { mutableStateOf("expense_by_category") }
    var dateFrom by remember { mutableStateOf("") }
    var dateTo by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Custom Query Builder", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        val queryTypes = listOf("expense_by_category" to "Expense by Category", "income_by_category" to "Income by Category", "balance_trend" to "Balance Trend", "expense_by_month" to "Expense by Month")
        queryTypes.forEach { (key, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = queryType == key, onClick = { queryType = key })
                Text(label)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = dateFrom, onValueChange = { dateFrom = it }, label = { Text("From") }, modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(value = dateTo, onValueChange = { dateTo = it }, label = { Text("To") }, modifier = Modifier.weight(1f), singleLine = true)
        }
        Button(onClick = { vm.runQuery(queryType, dateFrom, dateTo) }, modifier = Modifier.fillMaxWidth()) { Text("Run Query") }
    }
}

@Composable
private fun InsightDataTableTab(state: InsightExplorerUiState) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Data Table View", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        if (state.trendData.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No data", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            LazyColumn {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Month", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Expense", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Income", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    }
                    HorizontalDivider()
                }
                items(state.trendData) { point ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(point.month, modifier = Modifier.weight(1f))
                        Text("%.2f".format(point.expense), modifier = Modifier.weight(1f))
                        Text("%.2f".format(point.income), modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
