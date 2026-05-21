package com.ezbookkeeping.android.ui.screen.template

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
import com.ezbookkeeping.android.data.db.entity.TemplateEntity
import com.ezbookkeeping.android.util.AmountUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateListScreen(navController: NavController) {
    val vm: TemplateListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.templates)) }) }) { padding ->
        if (state.isLoading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else if (state.templates.isEmpty()) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text(stringResource(R.string.no_templates), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        else LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            items(state.templates, key = { it.id }) { tmpl ->
                ListItem(headlineContent = { Text(tmpl.name, fontWeight = FontWeight.Medium) }, supportingContent = { Text(tmpl.type.name) }, trailingContent = { Text(AmountUtil.format(tmpl.amount), fontWeight = FontWeight.Bold) }, modifier = Modifier.clickable { }.padding(horizontal = 8.dp))
            }
        }
    }
}
