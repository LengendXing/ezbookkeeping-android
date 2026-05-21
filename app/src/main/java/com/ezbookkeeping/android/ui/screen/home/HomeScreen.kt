package com.ezbookkeeping.android.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val vm: HomeViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )
    }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Month summary card (yellow background like original)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme())
                            MaterialTheme.colorScheme.primary else
                            MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 16.dp)) {
                        // Month label + "Expense"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.monthLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(" · ", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = stringResource(R.string.expense),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        // Large expense amount + eye toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (state.showAmount) AmountUtil.format(state.monthExpense) else "****",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { vm.toggleShowAmount() }) {
                                Icon(
                                    imageVector = if (state.showAmount) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        // Monthly income
                        Text(
                            text = "${stringResource(R.string.monthly_income)} ${if (state.showAmount) AmountUtil.format(state.monthIncome) else "****"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Quick links: Today / This Week / This Month / This Year
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        OverviewRow(
                            title = stringResource(R.string.today),
                            subtitle = state.todayDate,
                            income = state.todayIncome,
                            expense = state.todayExpense,
                            showAmount = state.showAmount,
                            onClick = { navController.navigate(Routes.TRANSACTION_LIST) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        OverviewRow(
                            title = stringResource(R.string.this_week),
                            subtitle = state.thisWeekRange,
                            income = state.thisWeekIncome,
                            expense = state.thisWeekExpense,
                            showAmount = state.showAmount,
                            onClick = { navController.navigate(Routes.TRANSACTION_LIST) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        OverviewRow(
                            title = stringResource(R.string.this_month),
                            subtitle = state.thisMonthRange,
                            income = state.monthIncome,
                            expense = state.monthExpense,
                            showAmount = state.showAmount,
                            onClick = { navController.navigate(Routes.TRANSACTION_LIST) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        OverviewRow(
                            title = stringResource(R.string.this_year),
                            subtitle = state.thisYearLabel,
                            income = state.thisYearIncome,
                            expense = state.thisYearExpense,
                            showAmount = state.showAmount,
                            onClick = { navController.navigate(Routes.TRANSACTION_LIST) }
                        )
                    }
                }
            }

            // Recent transactions
            item {
                Text(
                    text = stringResource(R.string.details),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.transactions.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_transactions), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(state.transactions, key = { it.id }) { tx ->
                    TransactionRow(tx) { navController.navigate(Routes.TRANSACTION_EDIT + "/${tx.id}") }
                }
            }
        }
    }
}

@Composable
private fun OverviewRow(
    title: String,
    subtitle: String,
    income: Double,
    expense: Double,
    showAmount: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "+${if (showAmount) AmountUtil.format(income) else "****"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "-${if (showAmount) AmountUtil.format(expense) else "****"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun TransactionRow(tx: TransactionEntity, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(tx.comment ?: tx.type.name) },
        supportingContent = { Text(tx.date) },
        trailingContent = {
            val color = when (tx.type) {
                TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary
            }
            val prefix = when (tx.type) {
                TransactionType.EXPENSE -> "-"
                TransactionType.INCOME -> "+"
                TransactionType.TRANSFER -> ""
            }
            Text("$prefix${AmountUtil.format(tx.sourceAmount)}", color = color, fontWeight = FontWeight.Bold)
        },
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp)
    )
}
