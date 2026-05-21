package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val ColorPalette = listOf(
    "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3",
    "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
    "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#607D8B",
    "#E8EAF6", "#FCE4EC", "#F3E5F5", "#EDE7F6", "#E8F5E9", "#FFF3E0",
    "#000000", "#333333", "#666666", "#999999", "#CCCCCC", "#FFFFFF"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSelectionSheet(
    visible: Boolean,
    title: String = "Select Color",
    initialColor: String = "",
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var selectedColor by remember(visible) { mutableStateOf(initialColor) }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                // Preview
                if (selectedColor.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = try { Color(android.graphics.Color.parseColor(selectedColor)) }
                                catch (_: Exception) { MaterialTheme.colorScheme.primary }
                        ) {}
                        Spacer(Modifier.width(12.dp))
                        Text(selectedColor, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ColorPalette) { colorHex ->
                        val isSelected = colorHex == selectedColor
                        val color = try { Color(android.graphics.Color.parseColor(colorHex)) }
                            catch (_: Exception) { MaterialTheme.colorScheme.surfaceVariant }
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .then(if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier),
                            shape = CircleShape,
                            color = color,
                            onClick = { selectedColor = colorHex }
                        ) {}
                    }
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onSelect(selectedColor); onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Confirm") }
            }
        }
    }
}
