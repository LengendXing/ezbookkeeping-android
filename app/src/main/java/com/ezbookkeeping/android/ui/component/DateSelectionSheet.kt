package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun DateSelectionSheetContent(
    initialDate: String = "",
    onDateSelected: (String) -> Unit
) {
    var calendar by remember {
        mutableStateOf(
            if (initialDate.isNotBlank()) {
                try { Calendar.getInstance().apply { time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(initialDate)!! } }
                catch (_: Exception) { Calendar.getInstance() }
            } else Calendar.getInstance()
        )
    }
    val monthFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) } }) { Text("<") }
        Text(monthFormatter.format(calendar.time), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
        TextButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, 1) } }) { Text(">") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionSheet(
    visible: Boolean,
    title: String = "Select Date",
    initialDate: String = "",
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var calendar by remember(visible) {
        mutableStateOf(
            if (initialDate.isNotBlank()) {
                try { Calendar.getInstance().apply { time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(initialDate)!! } }
                catch (_: Exception) { Calendar.getInstance() }
            } else Calendar.getInstance()
        )
    }
    val monthFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) } }) { Text("<") }
                    Text(monthFormatter.format(calendar.time), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                    TextButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, 1) } }) { Text(">") }
                }

                val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    dayHeaders.forEach { day ->
                        Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                val firstDayOfWeek = (calendar.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                }.get(Calendar.DAY_OF_WEEK) - 1
                val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val today = Calendar.getInstance()
                val selectedDay = if (initialDate.isNotBlank()) {
                    try {
                        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(initialDate)
                        val c = Calendar.getInstance(); c.time = parsed!!; c.get(Calendar.DAY_OF_MONTH)
                    } catch (_: Exception) { -1 }
                } else -1

                val rows = (firstDayOfWeek + daysInMonth + 6) / 7
                Column(modifier = Modifier.fillMaxWidth()) {
                    for (row in 0 until rows) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (col in 0..6) {
                                val dayNum = row * 7 + col - firstDayOfWeek + 1
                                if (dayNum in 1..daysInMonth) {
                                    val isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                        calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) && dayNum == today.get(Calendar.DAY_OF_MONTH)
                                    val isSelected = dayNum == selectedDay
                                    Box(
                                        modifier = Modifier.weight(1f).padding(2.dp).clickable {
                                            val dateStr = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, dayNum)
                                            onSelect(dateStr)
                                            onDismiss()
                                        },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            shape = MaterialTheme.shapes.small,
                                            color = when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.primaryContainer
                                                else -> MaterialTheme.colorScheme.surface
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    dayNum.toString(),
                                                    color = when {
                                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                                        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    },
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f).padding(2.dp).size(36.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
