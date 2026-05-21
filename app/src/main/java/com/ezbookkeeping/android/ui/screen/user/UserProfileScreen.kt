package com.ezbookkeeping.android.ui.screen.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(navController: NavController) {
    val vm: UserProfileViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("User Profile") }, navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.cancel)) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = state.username, onValueChange = {}, label = { Text(stringResource(R.string.username)) }, modifier = Modifier.fillMaxWidth(), singleLine = true, readOnly = true)
            OutlinedTextField(value = state.nickname, onValueChange = vm::onNicknameChange, label = { Text("Nickname") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = state.email, onValueChange = vm::onEmailChange, label = { Text(stringResource(R.string.email)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            // Language
            ListItem(headlineContent = { Text("Language") }, supportingContent = { Text(state.language) })
            // Default currency
            ListItem(headlineContent = { Text("Default Currency") }, supportingContent = { Text(state.defaultCurrency) })
            // First day of week
            ListItem(headlineContent = { Text("First Day of Week") }, supportingContent = { Text(state.firstDayOfWeek) })
            Spacer(Modifier.weight(1f))
            Button(onClick = vm::save, modifier = Modifier.fillMaxWidth().height(48.dp), enabled = !state.isLoading) { Text(stringResource(R.string.save)) }
        }
    }
}
