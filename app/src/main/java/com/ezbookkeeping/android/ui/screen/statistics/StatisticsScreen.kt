package com.ezbookkeeping.android.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController) {
    val vm: StatisticsViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.statistics)) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DateRange.values().forEach { range ->
                    FilterChip(
                        selected = state.dateRange == range,
                        onClick = { vm.setDateRange(range) },
                        label = { Text(range.label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    item { OverviewCard(state) }
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        Text("Expense by Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                    if (state.expenseByCategory.isNotEmpty()) {
                        item { PieChartSection(state.expenseByCategory) }
                        items(state.expenseByCategory) { stat -> StatRow(stat, MaterialTheme.colorScheme.error) }
                    } else {
                        item { Text("No expense data", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp)) }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        Text("Income by Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                    if (state.incomeByCategory.isNotEmpty()) {
                        item { PieChartSection(state.incomeByCategory) }
                        items(state.incomeByCategory) { stat -> StatRow(stat, MaterialTheme.colorScheme.primary) }
                    } else {
                        item { Text("No income data", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(state: StatisticsUiState) {
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

@Composable
private fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun PieChartSection(stats: List<CategoryStat>) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
        Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
            val totalAngle = 360f
            var startAngle = 0f
            val totalPct = stats.sumOf { it.percentage.toDouble() }.toFloat()
            stats.forEach { stat ->
                val sweep = if (totalPct > 0f) stat.percentage / totalPct * totalAngle else 0f
                Box(
                    Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(parseColor(stat.color).copy(alpha = 0.85f))
                )
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

@Composable
private fun StatRow(stat: CategoryStat, amountColor: Color) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(parseColor(stat.color)))
            Spacer(Modifier.width(8.dp))
            Text(stat.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            Text(AmountUtil.format(stat.amount), fontWeight = FontWeight.Bold, color = amountColor)
            Spacer(Modifier.width(8.dp))
            Text("${String.format("%.1f", stat.percentage)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        LinearProgressIndicator(
            progress = { stat.percentage / 100f },
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 2.dp),
            color = parseColor(stat.color),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

private fun parseColor(hex: String): Color {
    return try {
        val cleaned = hex.removePrefix("#")
        val colorVal = cleaned.toLong(16)
        when (cleaned.length) {
            6 -> Color(0xFF000000 or colorVal)
            8 -> Color(colorVal)
            else -> Color(0xFF6200EE)
        }
    } catch (_: Exception) {
        Color(0xFF6200EE)
    }
}
