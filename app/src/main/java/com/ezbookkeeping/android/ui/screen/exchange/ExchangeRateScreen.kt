package com.ezbookkeeping.android.ui.screen.exchange

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
import com.ezbookkeeping.android.data.db.entity.ExchangeRateEntity
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateScreen(navController: NavController) {
    val vm: ExchangeRateViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.exchange_rates)) }, actions = { TextButton(onClick = vm::refresh) { Text(stringResource(R.string.refresh)) } }) }) { padding ->
        if (state.isLoading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) { items(state.rates, key = { it.id }) { rate -> RateRow(rate) } }
    }
}

@Composable
fun RateRow(rate: ExchangeRateEntity) {
    ListItem(headlineContent = { Text(rate.currency, fontWeight = FontWeight.Medium) }, supportingContent = { Text(rate.source) }, trailingContent = { Text(AmountUtil.format(rate.rate, 4), fontWeight = FontWeight.Bold) })
}
