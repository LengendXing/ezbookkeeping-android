package com.ezbookkeeping.android.ui.screen.home

import androidx.compose.foundation.clickable
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
import com.ezbookkeeping.android.data.db.entity.TransactionEntity
import com.ezbookkeeping.android.data.db.entity.TransactionType
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val vm: HomeViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Summary card
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.expense), style = MaterialTheme.typography.labelSmall); Text(AmountUtil.format(state.totalExpense), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.income), style = MaterialTheme.typography.labelSmall); Text(AmountUtil.format(state.totalIncome), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                }
            }
            if (state.isLoading) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            else if (state.transactions.isEmpty()) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.no_transactions), color = MaterialTheme.colorScheme.onSurfaceVariant) } }
            else {
                LazyColumn(modifier = Modifier.fillMaxSize()) { items(state.transactions, key = { it.id }) { tx -> TransactionRow(tx) { navController.navigate(Routes.TRANSACTION_EDIT + "/${tx.id}") } } }
            }
        }
    }
}

@Composable
fun TransactionRow(tx: TransactionEntity, onClick: () -> Unit) {
    ListItem(headlineContent = { Text(tx.comment ?: tx.type.name) }, supportingContent = { Text(tx.date) }, trailingContent = {
        val color = when (tx.type) { TransactionType.EXPENSE -> MaterialTheme.colorScheme.error; TransactionType.INCOME -> MaterialTheme.colorScheme.primary; TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary }
        val prefix = when (tx.type) { TransactionType.EXPENSE -> "-"; TransactionType.INCOME -> "+"; TransactionType.TRANSFER -> "" }
        Text("$prefix${AmountUtil.format(tx.sourceAmount)}", color = color, fontWeight = FontWeight.Bold)
    }, modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp))
}
