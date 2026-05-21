package com.ezbookkeeping.android.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController) {
    val vm: StatisticsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.statistics)) },
            actions = {
                IconButton(onClick = { navController.navigate(Routes.STATISTICS_SETTINGS) }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Date range
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DateRange.entries.forEach { range ->
                    FilterChip(selected = state.dateRange == range, onClick = { vm.setDateRange(range) }, label = { Text(range.label, style = MaterialTheme.typography.labelSmall) })
                }
            }
            // Chart type
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ChartType.entries.forEach { type ->
                    FilterChip(selected = state.chartType == type, onClick = { vm.setChartType(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) })
                }
            }
            // Data type
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DataDataType.entries.forEach { dt ->
                    FilterChip(selected = state.dataType == dt, onClick = { vm.setDataType(dt) },
                        label = { Text(dt.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) })
                }
            }
            // Aggregation
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DateAggregation.entries.forEach { agg ->
                    FilterChip(selected = state.aggregation == agg, onClick = { vm.setAggregation(agg) },
                        label = { Text("By ${agg.name.lowercase().replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.labelSmall) })
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    item { OverviewCard(state) }
                    item { Spacer(Modifier.height(12.dp)) }

                    val statsToShow = when (state.dataType) {
                        DataDataType.EXPENSE -> state.expenseByCategory
                        DataDataType.INCOME -> state.incomeByCategory
                        DataDataType.BOTH -> state.expenseByCategory + state.incomeByCategory
                    }
                    val sortedStats = when (state.sortMethod) {
                        SortMethod.AMOUNT_ASC -> statsToShow.sortedBy { it.amount }
                        SortMethod.AMOUNT_DESC -> statsToShow.sortedByDescending { it.amount }
                    }
                    val sectionLabel = when (state.dataType) {
                        DataDataType.EXPENSE -> "Expense by Category"
                        DataDataType.INCOME -> "Income by Category"
                        DataDataType.BOTH -> "All Categories"
                    }

                    item { Text(sectionLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary) }
                    if (sortedStats.isNotEmpty()) {
                        item { PieChartSection(sortedStats) }
                        items(sortedStats) { stat -> StatRow(stat, if (state.dataType == DataDataType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
                    } else {
                        item { Text("No data", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
            }
        }
    }
}

@Composable private fun OverviewCard(state: StatisticsUiState) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(16.dp)) {
            Text("Summary", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatBadge("Expense", AmountUtil.format(state.totalExpense), MaterialTheme.colorScheme.error)
                StatBadge("Income", AmountUtil.format(state.totalIncome), MaterialTheme.colorScheme.primary)
                StatBadge("Transfer", AmountUtil.format(state.totalTransfer), MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable private fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable private fun PieChartSection(stats: List<CategoryStat>) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
        Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
            val totalPct = stats.sumOf { it.percentage.toDouble() }.toFloat()
            var startAngle = 0f
            stats.forEach { stat ->
                val sweep = if (totalPct > 0f) stat.percentage / totalPct * 360f else 0f
                Box(Modifier.size(120.dp).clip(CircleShape).background(parseColor(stat.color).copy(alpha = 0.85f)))
                startAngle += sweep
            }
            Box(Modifier.size(60.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(AmountUtil.format(stats.sumOf { it.amount }), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable private fun StatRow(stat: CategoryStat, amountColor: Color) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(parseColor(stat.color)))
            Spacer(Modifier.width(8.dp))
            Text(stat.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            Text(AmountUtil.format(stat.amount), fontWeight = FontWeight.Bold, color = amountColor)
            Spacer(Modifier.width(8.dp))
            Text("${String.format("%.1f", stat.percentage)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        LinearProgressIndicator(progress = { stat.percentage / 100f }, modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 2.dp), color = parseColor(stat.color), trackColor = MaterialTheme.colorScheme.surfaceVariant)
    }
}

private fun parseColor(hex: String): Color = try { val c = hex.removePrefix("#"); val v = c.toLong(16); if (c.length == 6) Color(0xFF000000 or v) else Color(v) } catch (_: Exception) { Color(0xFF6200EE) }
