package com.ezbookkeeping.android.ui.screen.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AboutScreenPlaceholder() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("About - Coming in Phase 2", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
