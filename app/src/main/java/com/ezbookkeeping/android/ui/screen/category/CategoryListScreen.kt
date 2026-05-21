package com.ezbookkeeping.android.ui.screen.category

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R
import com.ezbookkeeping.android.data.db.entity.CategoryEntity
import com.ezbookkeeping.android.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(navController: NavController) {
    val vm: CategoryListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.categories)) }) }, floatingActionButton = { FloatingActionButton(onClick = { navController.navigate(Routes.CATEGORY_EDIT) }) { Icon(Icons.Default.Add, "Add category") } }) { padding ->
        if (state.isLoading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else LazyColumn(Modifier.fillMaxSize().padding(padding)) { items(state.categories, key = { it.id }) { cat -> CategoryRow(cat) { navController.navigate(Routes.CATEGORY_EDIT + "/${cat.id}") } } }
    }
}

@Composable
fun CategoryRow(cat: CategoryEntity, onClick: () -> Unit) {
    ListItem(headlineContent = { Text(cat.name) }, supportingContent = { Text(cat.type.name) }, modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp))
}
