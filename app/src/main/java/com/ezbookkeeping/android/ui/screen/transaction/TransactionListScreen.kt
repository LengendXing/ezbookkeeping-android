package com.ezbookkeeping.android.ui.screen.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun TransactionListScreen(navController: NavController) {
    val vm: com.ezbookkeeping.android.ui.screen.home.HomeViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.transactions)) }) }, floatingActionButton = { FloatingActionButton(onClick = { navController.navigate(Routes.TRANSACTION_EDIT) }) { Icon(Icons.Default.Add, "New transaction") } }) { padding ->
        if (state.isLoading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else LazyColumn(Modifier.fillMaxSize().padding(padding)) { items(state.transactions, key = { it.id }) { tx ->
            ListItem(headlineContent = { Text(tx.comment ?: tx.type.name) }, supportingContent = { Text(tx.date) }, trailingContent = {
                val color = when (tx.type) { TransactionType.EXPENSE -> MaterialTheme.colorScheme.error; TransactionType.INCOME -> MaterialTheme.colorScheme.primary; TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary }
                val prefix = when (tx.type) { TransactionType.EXPENSE -> "-"; TransactionType.INCOME -> "+"; TransactionType.TRANSFER -> "" }
                Text("$prefix${AmountUtil.format(tx.sourceAmount)}", color = color, fontWeight = FontWeight.Bold)
            }, modifier = Modifier.clickable { navController.navigate(Routes.TRANSACTION_EDIT + "/${tx.id}") }.padding(horizontal = 8.dp))
        } }
    }
}
