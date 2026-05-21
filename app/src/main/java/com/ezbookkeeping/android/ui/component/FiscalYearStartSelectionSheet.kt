package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val MONTH_LABELS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiscalYearStartSelectionSheet(
    visible: Boolean,
    initialMonth: Int = 1,
    onDismiss: () -> Unit,
    onSelect: (month: Int) -> Unit
) {
    if (!visible) return
    var selectedMonth by remember { mutableStateOf(initialMonth) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Fiscal Year Start Month", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text("Select the first month of your fiscal year", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(180.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items((1..12).toList()) { m ->
                    val selected = m == selectedMonth
                    OutlinedButton(
                        onClick = { selectedMonth = m },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (selected) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonColors(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(MONTH_LABELS[m - 1], color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onSelect(selectedMonth) }) { Text("OK") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
