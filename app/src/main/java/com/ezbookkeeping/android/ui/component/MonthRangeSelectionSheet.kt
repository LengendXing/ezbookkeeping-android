package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthRangeSelectionSheet(
    visible: Boolean,
    initialStartYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    initialStartMonth: Int = 1,
    initialEndYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    initialEndMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    onDismiss: () -> Unit,
    onSelect: (startYear: Int, startMonth: Int, endYear: Int, endMonth: Int) -> Unit
) {
    if (!visible) return
    var startYear by remember { mutableStateOf(initialStartYear) }
    var startMonth by remember { mutableStateOf(initialStartMonth) }
    var endYear by remember { mutableStateOf(initialEndYear) }
    var endMonth by remember { mutableStateOf(initialEndMonth) }
    var editingStart by remember { mutableStateOf(true) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Select Month Range", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = editingStart, onClick = { editingStart = true }, label = { Text("From") }, modifier = Modifier.weight(1f))
                FilterChip(selected = !editingStart, onClick = { editingStart = false }, label = { Text("To") }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))

            val currentYear = if (editingStart) startYear else endYear
            val currentMonth = if (editingStart) startMonth else endMonth

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (editingStart) startYear-- else endYear-- }) { Text("<") }
                Text("$currentYear", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = { if (editingStart) startYear++ else endYear++ }) { Text(">") }
            }
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                (1..12).chunked(4).forEach { row ->
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        row.forEach { m ->
                            val selected = m == currentMonth
                            OutlinedButton(
                                onClick = {
                                    if (editingStart) startMonth = m else endMonth = m
                                },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                colors = if (selected) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("%02d".format(m), color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            Text("%04d-%02d to %04d-%02d".format(startYear, startMonth, endYear, endMonth),
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onSelect(startYear, startMonth, endYear, endMonth) }) { Text("OK") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
