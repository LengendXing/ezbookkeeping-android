package com.ezbookkeeping.android.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class IconEntry(val name: String, val icon: ImageVector)

private val IconEntries: List<IconEntry> = listOf(
    IconEntry("Home", Icons.Default.Home),
    IconEntry("Star", Icons.Default.Star),
    IconEntry("Favorite", Icons.Default.Favorite),
    IconEntry("ShoppingCart", Icons.Default.ShoppingCart),
    IconEntry("Restaurant", Icons.Default.Restaurant),
    IconEntry("LocalGasStation", Icons.Default.LocalGasStation),
    IconEntry("DirectionsCar", Icons.Default.DirectionsCar),
    IconEntry("Flight", Icons.Default.Flight),
    IconEntry("Hotel", Icons.Default.Hotel),
    IconEntry("MedicalServices", Icons.Default.MedicalServices),
    IconEntry("School", Icons.Default.School),
    IconEntry("Work", Icons.Default.Work),
    IconEntry("PhoneAndroid", Icons.Default.PhoneAndroid),
    IconEntry("Computer", Icons.Default.Computer),
    IconEntry("VideogameAsset", Icons.Default.VideogameAsset),
    IconEntry("Movie", Icons.Default.Movie),
    IconEntry("MusicNote", Icons.Default.MusicNote),
    IconEntry("SportsSoccer", Icons.Default.SportsSoccer),
    IconEntry("FitnessCenter", Icons.Default.FitnessCenter),
    IconEntry("Pets", Icons.Default.Pets),
    IconEntry("ChildCare", Icons.Default.ChildCare),
    IconEntry("LocalGroceryStore", Icons.Default.LocalGroceryStore),
    IconEntry("LocalMall", Icons.Default.LocalMall),
    IconEntry("LocalBar", Icons.Default.LocalBar),
    IconEntry("Cake", Icons.Default.Cake),
    IconEntry("CardGiftcard", Icons.Default.CardGiftcard),
    IconEntry("Savings", Icons.Default.Savings),
    IconEntry("AccountBalance", Icons.Default.AccountBalance),
    IconEntry("AttachMoney", Icons.Default.AttachMoney),
    IconEntry("Payments", Icons.Default.Payments),
    IconEntry("TrendingUp", Icons.Default.TrendingUp),
    IconEntry("TrendingDown", Icons.Default.TrendingDown),
    IconEntry("ShowChart", Icons.Default.ShowChart),
    IconEntry("Receipt", Icons.Default.Receipt),
    IconEntry("CreditCard", Icons.Default.CreditCard),
    IconEntry("LocalAtm", Icons.Default.LocalAtm),
    IconEntry("Category", Icons.Default.Category),
    IconEntry("Label", Icons.Default.Label),
    IconEntry("Bookmark", Icons.Default.Bookmark),
    IconEntry("Flag", Icons.Default.Flag),
    IconEntry("Event", Icons.Default.Event),
    IconEntry("Schedule", Icons.Default.Schedule),
    IconEntry("Timer", Icons.Default.Timer),
    IconEntry("Alarm", Icons.Default.Alarm),
    IconEntry("Today", Icons.Default.Today),
    IconEntry("CalendarMonth", Icons.Default.CalendarMonth),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconSelectionSheet(
    visible: Boolean,
    title: String = "Select Icon",
    initialIcon: String = "",
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var selectedIcon by remember(visible) { mutableStateOf(initialIcon) }

    if (visible) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(IconEntries) { entry ->
                        val isSelected = entry.name == selectedIcon
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            onClick = { selectedIcon = entry.name }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = entry.icon,
                                    contentDescription = entry.name,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onSelect(selectedIcon); onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Confirm") }
            }
        }
    }
}
