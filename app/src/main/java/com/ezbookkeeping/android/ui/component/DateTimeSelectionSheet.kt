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
fun DateTimeSelectionSheet(
    visible: Boolean,
    title: String = "Select Date & Time",
    initialDate: String = "",
    initialTime: String = "",
    onDismiss: () -> Unit,
    onSelect: (date: String, time: String) -> Unit
) {
    var selectedDate by remember(visible) { mutableStateOf(initialDate.ifBlank { today() }) }
    var selectedHour by remember(visible) {
        mutableStateOf(initialTime.substringBefore(":").toIntOrNull() ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
    }
    var selectedMinute by remember(visible) {
        mutableStateOf(initialTime.substringAfter(":").toIntOrNull() ?: Calendar.getInstance().get(Calendar.MINUTE))
    }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))

                // Inline date selection (simplified)
                var calendar by remember {
                    mutableStateOf(
                        if (selectedDate.isNotBlank()) {
                            try { Calendar.getInstance().apply { time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)!! } }
                            catch (_: Exception) { Calendar.getInstance() }
                        } else Calendar.getInstance()
                    )
                }
                val monthFmt = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) } }) { Text("<") }
                    Text(monthFmt.format(calendar.time), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                    TextButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, 1) } }) { Text(">") }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Time", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    LazyColumn(modifier = Modifier.weight(1f).height(120.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        items(24) { h ->
                            val selected = h == selectedHour
                            Text(
                                String.format("%02d", h),
                                modifier = Modifier.fillMaxWidth().clickable { selectedHour = h }.padding(vertical = 6.dp),
                                textAlign = TextAlign.Center,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Text(":", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    LazyColumn(modifier = Modifier.weight(1f).height(120.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        items(60) { m ->
                            val selected = m == selectedMinute
                            Text(
                                String.format("%02d", m),
                                modifier = Modifier.fillMaxWidth().clickable { selectedMinute = m }.padding(vertical = 6.dp),
                                textAlign = TextAlign.Center,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedDate = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
                        onSelect(selectedDate, String.format("%02d:%02d", selectedHour, selectedMinute))
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Confirm") }
            }
        }
    }
}

private fun today(): String {
    val c = Calendar.getInstance()
    return String.format("%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
}
