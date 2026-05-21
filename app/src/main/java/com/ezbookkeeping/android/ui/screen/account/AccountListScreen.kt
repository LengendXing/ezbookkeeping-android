package com.ezbookkeeping.android.ui.screen.account

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
import com.ezbookkeeping.android.data.db.entity.AccountEntity
import com.ezbookkeeping.android.ui.navigation.Routes
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountListScreen(navController: NavController) {
    val vm: AccountListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.accounts)) }) }, floatingActionButton = { FloatingActionButton(onClick = { navController.navigate(Routes.ACCOUNT_EDIT) }) { Icon(Icons.Default.Add, "Add account") } }) { padding ->
        if (state.isLoading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else LazyColumn(Modifier.fillMaxSize().padding(padding)) { items(state.accounts, key = { it.id }) { account -> AccountRow(account) { navController.navigate(Routes.ACCOUNT_EDIT + "/${account.id}") } } }
    }
}

@Composable
fun AccountRow(account: AccountEntity, onClick: () -> Unit) {
    ListItem(headlineContent = { Text(account.name, fontWeight = FontWeight.Medium) }, supportingContent = { Text("${account.currency} · ${account.type.name}") }, trailingContent = { Text(AmountUtil.format(account.balance), fontWeight = FontWeight.Bold) }, modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp))
}
