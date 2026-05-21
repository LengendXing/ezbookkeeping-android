package com.ezbookkeeping.android.ui.screen.tag

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ezbookkeeping.android.R

@Composable
fun TagEditScreenPlaceholder() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(stringResource(R.string.tag_edit_placeholder), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
