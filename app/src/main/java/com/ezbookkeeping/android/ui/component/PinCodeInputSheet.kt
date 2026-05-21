package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinCodeInputSheet(
    visible: Boolean,
    title: String = "Enter PIN",
    pinLength: Int = 4,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onError: (() -> Unit)? = null
) {
    var pin by remember(visible) { mutableStateOf("") }
    var error by remember(visible) { mutableStateOf(false) }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(16.dp))

                // PIN dots
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(pinLength) { i ->
                        Surface(
                            modifier = Modifier.size(16.dp),
                            shape = MaterialTheme.shapes.extraSmall,
                            color = when {
                                error -> MaterialTheme.colorScheme.error
                                i < pin.length -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.outline
                            }
                        ) {}
                    }
                }
                if (error) {
                    Text("Incorrect PIN", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(Modifier.height(16.dp))

                val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "⌫")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(keys) { key ->
                        if (key.isEmpty()) {
                            Spacer(Modifier.height(52.dp))
                        } else {
                            OutlinedButton(
                                onClick = {
                                    error = false
                                    when (key) {
                                        "⌫" -> { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
                                        else -> {
                                            if (pin.length < pinLength) {
                                                pin += key
                                                if (pin.length == pinLength) {
                                                    onConfirm(pin)
                                                }
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.height(52.dp).fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(key, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { pin = ""; error = false }) { Text("Clear") }
            }
        }
    }
}
