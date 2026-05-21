package com.ezbookkeeping.android.ui.screen.tag

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ezbookkeeping.android.data.db.entity.TagGroupEntity
import com.ezbookkeeping.android.data.db.entity.TagEntity

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun TagListScreen(navController: NavController) {
    val vm: TagListViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Tags") }) }) { padding ->
        if (state.isLoading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        else LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            items(state.groups, key = { it.id }) { group ->
                Text(group.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
                val groupTags = state.tags.filter { it.groupId == group.id }
                FlowRow { groupTags.forEach { tag -> AssistChip(onClick = { }, label = { Text(tag.name) }, modifier = Modifier.padding(2.dp)) } }
            }
        }
    }
}
