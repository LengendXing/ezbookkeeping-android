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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightExplorerScreen(navController: NavController) {
    val vm: InsightExplorerViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Data Insights") }, navigationIcon = {
            TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) }
        })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    ListItem(
                        headlineContent = { Text(insight.title) },
                        supportingContent = { Text(insight.trend, color = if (insight.isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) },
                        trailingContent = { Text(insight.value, fontWeight = FontWeight.Bold) }
                    )
                }

                item {
                    Text("Expense Trend", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.trendData.forEach { point ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(point.month, modifier = Modifier.weight(0.15f), style = MaterialTheme.typography.bodySmall)
                                    LinearProgressIndicator(progress = { ((point.expense / 6000.0).coerceIn(0.0, 1.0)).toFloat() }, modifier = Modifier.weight(0.4f).height(8.dp), color = MaterialTheme.colorScheme.error)
                                    LinearProgressIndicator(progress = { ((point.income / 6000.0).coerceIn(0.0, 1.0)).toFloat() }, modifier = Modifier.weight(0.4f).height(8.dp), color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("", modifier = Modifier.weight(0.15f))
                                Text("Expense", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                Text("Income", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                item {
                    Text("Top Expense Categories", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }

                items(state.topExpenseCategories) { cat ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(cat.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        LinearProgressIndicator(progress = { cat.percentage / 100f }, modifier = Modifier.weight(0.5f).height(8.dp).padding(end = 8.dp))
                        Text("${cat.percentage}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
