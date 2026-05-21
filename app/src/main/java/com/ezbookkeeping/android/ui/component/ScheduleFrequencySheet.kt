package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ScheduleFrequency(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    BIWEEKLY("Biweekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    CUSTOM("Custom Days")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleFrequencySheet(
    visible: Boolean,
    selected: ScheduleFrequency = ScheduleFrequency.MONTHLY,
    onDismiss: () -> Unit,
    onSelect: (ScheduleFrequency) -> Unit
) {
    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Schedule Frequency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(ScheduleFrequency.entries) { freq ->
                        Row(
                            Modifier.fillMaxWidth().clickable { onSelect(freq) }.padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = freq == selected, onClick = { onSelect(freq) })
                            Spacer(Modifier.width(8.dp))
                            Text(freq.label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
