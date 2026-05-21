package com.ezbookkeeping.android.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object AmountUtil {
    fun format(amount: Double, decimalPlaces: Int = 2): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val df = DecimalFormat("#,##0.${"#".repeat(decimalPlaces)}", symbols)
        return df.format(amount)
    }
}
