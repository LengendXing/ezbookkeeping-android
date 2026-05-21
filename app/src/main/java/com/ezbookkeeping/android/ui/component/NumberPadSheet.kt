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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberPadSheet(
    visible: Boolean,
    initialAmount: String = "",
    currencySymbol: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var amount by remember(visible) { mutableStateOf(initialAmount) }
    var hasDecimal by remember(visible) { mutableStateOf(initialAmount.contains(".")) }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display area
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (currencySymbol.isNotEmpty()) {
                        Text(
                            currencySymbol,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = if (amount.isEmpty()) "0" else amount,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }

                val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "⌫")

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(keys) { key ->
                        OutlinedButton(
                            onClick = {
                                when (key) {
                                    "⌫" -> {
                                        if (amount.isNotEmpty()) {
                                            val dropped = amount.dropLast(1)
                                            hasDecimal = dropped.contains(".")
                                            amount = dropped
                                        }
                                    }
                                    "." -> {
                                        if (!hasDecimal) {
                                            amount += "."
                                            hasDecimal = true
                                        }
                                    }
                                    else -> {
                                        if (key == "0" && amount == "0") return@OutlinedButton
                                        if (amount == "0" && key != ".") {
                                            amount = key
                                        } else {
                                            val decimalPart = if (hasDecimal) {
                                                amount.substringAfter(".", "").length
                                            } else 0
                                            if (hasDecimal && decimalPart >= 2) return@OutlinedButton
                                            amount += key
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

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onConfirm(amount) },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}
