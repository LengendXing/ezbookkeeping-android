package com.ezbookkeeping.android.ui.screen.category

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CategoryEditScreenPlaceholder() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Category Edit - Coming in Phase 2", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
