package com.ezbookkeeping.android.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
fun parseColor(hex: String): Color = try {
    val c = hex.removePrefix("#")
    val v = c.toLong(16)
    if (c.length == 6) Color(0xFF000000 or v) else Color(v)
} catch (_: Exception) { Color(0xFF6200EE) }
