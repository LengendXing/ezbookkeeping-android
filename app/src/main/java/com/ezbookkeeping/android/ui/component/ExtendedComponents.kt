package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSheet(visible: Boolean, initialLat: Double = 0.0, initialLng: Double = 0.0, onDismiss: () -> Unit, onSelect: (Double, Double) -> Unit) {
    if (!visible) return
    var lat by remember { mutableStateOf(initialLat.toString()) }
    var lng by remember { mutableStateOf(initialLng.toString()) }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Select Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitude") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = lng, onValueChange = { lng = it }, label = { Text("Longitude") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            Text("Map view placeholder - integrate Google Maps SDK for full functionality", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onSelect(lat.toDoubleOrNull() ?: 0.0, lng.toDoubleOrNull() ?: 0.0) }) { Text("OK") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationSheet(visible: Boolean, title: String = "", message: String = "", onDismiss: () -> Unit) {
    if (!visible) return
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            if (title.isNotBlank()) Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("OK") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItemSelectionPopup(visible: Boolean, items: List<String>, selectedIndex: Int = -1, onDismiss: () -> Unit, onSelect: (Int) -> Unit) {
    if (!visible) return
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            items.forEachIndexed { index, item ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = index == selectedIndex, onClick = { onSelect(index) })
                    Spacer(Modifier.width(8.dp))
                    Text(item)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ItemIcon(icon: String, color: String, modifier: Modifier = Modifier) {
    val bgColor = try { val c = color.removePrefix("#"); val v = c.toLong(16); if (c.length == 6) Color(0xFF000000 or v) else Color(v) } catch (_: Exception) { Color(0xFF6200EE) }
    Box(modifier.size(36.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
        Text(icon, fontSize = 16.sp, color = Color.White)
    }
}

@Composable
fun LanguageSelectButton(currentLang: String, onLangChange: (String) -> Unit) {
    val languages = listOf("en" to "English", "zh" to "中文", "ja" to "日本語")
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text(languages.find { it.first == currentLang }?.second ?: currentLang) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languages.forEach { (code, name) ->
                DropdownMenuItem(text = { Text(name) }, onClick = { onLangChange(code); expanded = false })
            }
        }
    }
}

@Composable
fun TrendsBarChart(data: List<Pair<String, Float>>, modifier: Modifier = Modifier, barColor: androidx.compose.ui.graphics.Color = androidx.compose.material3.MaterialTheme.colorScheme.primary) {
    if (data.isEmpty()) return
    val maxVal = data.maxOf { it.second }.coerceAtLeast(0.01f)
    Column(modifier.fillMaxWidth()) {
        data.forEach { (label, value) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(label, modifier = Modifier.weight(0.2f), style = MaterialTheme.typography.labelSmall)
                LinearProgressIndicator(progress = { (value / maxVal).coerceIn(0f, 1f) }, modifier = Modifier.weight(0.7f).height(12.dp), color = barColor)
                Spacer(Modifier.width(4.dp))
                Text("%.0f".format(value), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.1f))
            }
        }
    }
}

@Composable
fun AccountBalanceTrendsBarChart(data: List<Triple<String, Float, Float>>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return
    val maxVal = data.maxOf { maxOf(it.second, it.third) }.coerceAtLeast(0.01f)
    Column(modifier.fillMaxWidth()) {
        data.forEach { (label, income, expense) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(label, modifier = Modifier.weight(0.15f), style = MaterialTheme.typography.labelSmall)
                LinearProgressIndicator(progress = { (expense / maxVal).coerceIn(0f, 1f) }, modifier = Modifier.weight(0.38f).height(8.dp), color = MaterialTheme.colorScheme.error)
                LinearProgressIndicator(progress = { (income / maxVal).coerceIn(0f, 1f) }, modifier = Modifier.weight(0.38f).height(8.dp), color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(0.15f))
            Text("Expense", modifier = Modifier.weight(0.38f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            Text("Income", modifier = Modifier.weight(0.38f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ListNumberInput(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = modifier, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIImageRecognitionSheet(visible: Boolean, onDismiss: () -> Unit, onResult: (String, Double) -> Unit) {
    if (!visible) return
    var isProcessing by remember { mutableStateOf(false) }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("AI Image Recognition", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text("Take a photo or select an image to auto-fill transaction details", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { isProcessing = true }, modifier = Modifier.fillMaxWidth()) { Text("Select Image") }
            Spacer(Modifier.height(8.dp))
            if (isProcessing) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("Processing...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceTransactionSheet(visible: Boolean, onDismiss: () -> Unit, onResult: (String, Double) -> Unit) {
    if (!visible) return
    var isListening by remember { mutableStateOf(false) }
    var transcript by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Voice Input", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            FloatingActionButton(onClick = { isListening = !isListening }, containerColor = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) {
                Text(if (isListening) "Stop" else "Mic", color = androidx.compose.ui.graphics.Color.White)
            }
            Spacer(Modifier.height(12.dp))
            if (isListening) {
                Text("Listening...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            if (transcript.isNotBlank()) {
                Text(transcript, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
