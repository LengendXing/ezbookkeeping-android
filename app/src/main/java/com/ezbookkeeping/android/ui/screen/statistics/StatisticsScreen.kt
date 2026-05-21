package com.ezbookkeeping.android.ui.screen.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StatisticsScreenPlaceholder() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Statistics - Coming in Phase 2", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
