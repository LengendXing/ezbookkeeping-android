package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelectionSheet(
    visible: Boolean,
    title: String = "Select Date Range",
    initialStartDate: String = "",
    initialEndDate: String = "",
    onDismiss: () -> Unit,
    onSelect: (startDate: String, endDate: String) -> Unit
) {
    var startDate by remember(visible) { mutableStateOf(initialStartDate.ifBlank { today() }) }
    var endDate by remember(visible) { mutableStateOf(initialEndDate.ifBlank { today() }) }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(16.dp))

                // Start date
                Text("Start Date", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                DateSelectionSheet(
                    visible = true,
                    title = "",
                    initialDate = startDate,
                    onDismiss = {},
                    onSelect = { startDate = it }
                )

                Spacer(Modifier.height(16.dp))

                // End date
                Text("End Date", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                DateSelectionSheet(
                    visible = true,
                    title = "",
                    initialDate = endDate,
                    onDismiss = {},
                    onSelect = { endDate = it }
                )

                Spacer(Modifier.height(16.dp))

                // Quick ranges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickRangeChip("This Week") {
                        val c = Calendar.getInstance()
                        c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek)
                        startDate = fmt(c)
                        c.add(Calendar.DAY_OF_WEEK, 6)
                        endDate = fmt(c)
                    }
                    QuickRangeChip("This Month") {
                        val c = Calendar.getInstance()
                        c.set(Calendar.DAY_OF_MONTH, 1)
                        startDate = fmt(c)
                        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
                        endDate = fmt(c)
                    }
                    QuickRangeChip("This Year") {
                        val c = Calendar.getInstance()
                        c.set(Calendar.DAY_OF_YEAR, 1)
                        startDate = fmt(c)
                        c.set(Calendar.MONTH, 11)
                        c.set(Calendar.DAY_OF_MONTH, 31)
                        endDate = fmt(c)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { onSelect(startDate, endDate); onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Confirm") }
            }
        }
    }
}

@Composable
private fun QuickRangeChip(label: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.height(36.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

private fun today(): String {
    val c = Calendar.getInstance()
    return String.format("%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
}

private fun fmt(c: Calendar): String {
    return String.format("%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
}
