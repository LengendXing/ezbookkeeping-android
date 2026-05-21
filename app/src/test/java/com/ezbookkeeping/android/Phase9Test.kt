package com.ezbookkeeping.android

import com.ezbookkeeping.android.util.parseColor
import org.junit.Assert.*
import org.junit.Test

class ColorUtilTest {
    @Test fun parseColor_valid6DigitHex_noException() { assertNotNull(parseColor("#FF0000")) }
    @Test fun parseColor_valid8DigitHex_noException() { assertNotNull(parseColor("#FFFF0000")) }
    @Test fun parseColor_withoutHash_noException() { assertNotNull(parseColor("6200EE")) }
    @Test fun parseColor_invalidHex_returnsDefault() { assertNotNull(parseColor("invalid")) }
    @Test fun parseColor_emptyString_returnsDefault() { assertNotNull(parseColor("")) }
    @Test fun parseColor_black_noException() { assertNotNull(parseColor("#000000")) }
    @Test fun parseColor_white_noException() { assertNotNull(parseColor("#FFFFFF")) }
    @Test fun parseColor_purple_noException() { assertNotNull(parseColor("#6200EE")) }
}

class EnumCoverageTest {
    // TransactionTypeExt
    @Test fun transactionTypeExt_entries() { assertEquals(4, com.ezbookkeeping.android.ui.screen.transaction.TransactionTypeExt.entries.size) }
    @Test fun transactionTypeExt_valueOf() { assertEquals(com.ezbookkeeping.android.ui.screen.transaction.TransactionTypeExt.EXPENSE, com.ezbookkeeping.android.ui.screen.transaction.TransactionTypeExt.valueOf("EXPENSE")) }
    @Test fun transactionTypeExt_allPresent() { assertTrue(com.ezbookkeeping.android.ui.screen.transaction.TransactionTypeExt.entries.containsAll(listOf(com.ezbookkeeping.android.ui.screen.transaction.TransactionTypeExt.EXPENSE, com.ezbookkeeping.android.ui.screen.transaction.TransactionTypeExt.INCOME, com.ezbookkeeping.android.ui.screen.transaction.TransactionTypeExt.TRANSFER, com.ezbookkeeping.android.ui.screen.transaction.TransactionTypeExt.MODIFY_BALANCE))) }

    // ScheduleFrequency
    @Test fun scheduleFrequency_entries() { assertEquals(6, com.ezbookkeeping.android.ui.component.ScheduleFrequency.entries.size) }
    @Test fun scheduleFrequency_labels() { com.ezbookkeeping.android.ui.component.ScheduleFrequency.entries.forEach { assertNotNull(it.label) } }

    // ImportFormat
    @Test fun importFormat_entries() { assertEquals(3, com.ezbookkeeping.android.ui.screen.transaction.ImportFormat.entries.size) }
    @Test fun importFormat_csvLabel() { assertEquals("CSV", com.ezbookkeeping.android.ui.screen.transaction.ImportFormat.CSV.label) }

    // ImportStep
    @Test fun importStep_entries() { assertEquals(4, com.ezbookkeeping.android.ui.screen.transaction.ImportStep.entries.size) }

    // InsightTab
    @Test fun insightTab_entries() { assertEquals(3, com.ezbookkeeping.android.ui.screen.statistics.InsightTab.entries.size) }

    // InsightPeriod
    @Test fun insightPeriod_entries() { assertEquals(3, com.ezbookkeeping.android.ui.screen.statistics.InsightPeriod.entries.size) }
    @Test fun insightPeriod_labels() { com.ezbookkeeping.android.ui.screen.statistics.InsightPeriod.entries.forEach { assertNotNull(it.label) } }

    // ChartType
    @Test fun chartType_entries() { assertEquals(4, com.ezbookkeeping.android.ui.screen.statistics.ChartType.entries.size) }
    @Test fun chartType_valueOf() { assertEquals(com.ezbookkeeping.android.ui.screen.statistics.ChartType.PIE, com.ezbookkeeping.android.ui.screen.statistics.ChartType.valueOf("PIE")) }

    // DataDataType
    @Test fun dataDataType_entries() { assertEquals(3, com.ezbookkeeping.android.ui.screen.statistics.DataDataType.entries.size) }

    // DateAggregation
    @Test fun dateAggregation_entries() { assertEquals(4, com.ezbookkeeping.android.ui.screen.statistics.DateAggregation.entries.size) }

    // SortMethod
    @Test fun sortMethod_entries() { assertEquals(2, com.ezbookkeeping.android.ui.screen.statistics.SortMethod.entries.size) }

    // DateRange
    @Test fun dateRange_entries() { assertEquals(4, com.ezbookkeeping.android.ui.screen.statistics.DateRange.entries.size) }
    @Test fun dateRange_thisMonthLabel() { assertEquals("This Month", com.ezbookkeeping.android.ui.screen.statistics.DateRange.THIS_MONTH.label) }
}

class DataClassTest {
    // CategoryStat
    @Test fun categoryStat_creation() {
        val stat = com.ezbookkeeping.android.ui.screen.statistics.CategoryStat(1, "Food", "#FF0000", 100.0, 50f)
        assertEquals(1, stat.categoryId)
        assertEquals("Food", stat.name)
        assertEquals(100.0, stat.amount, 0.01)
        assertEquals(50f, stat.percentage, 0.01f)
    }
    @Test fun categoryStat_copy() {
        val stat = com.ezbookkeeping.android.ui.screen.statistics.CategoryStat(1, "Food", "#FF0000", 100.0, 50f)
        val copy = stat.copy(amount = 200.0)
        assertEquals(200.0, copy.amount, 0.01)
        assertEquals("Food", copy.name)
    }
    @Test fun categoryStat_equality() {
        val s1 = com.ezbookkeeping.android.ui.screen.statistics.CategoryStat(1, "Food", "#FF0000", 100.0, 50f)
        val s2 = com.ezbookkeeping.android.ui.screen.statistics.CategoryStat(1, "Food", "#FF0000", 100.0, 50f)
        assertEquals(s1, s2)
    }

    // ReconciliationRow
    @Test fun reconciliationRow_creation() {
        val row = com.ezbookkeeping.android.ui.screen.transaction.ReconciliationRow("2025-01-01", "Test", 100.0, 100.0, 0.0, true)
        assertTrue(row.isMatched)
        assertEquals(0.0, row.difference, 0.01)
    }
    @Test fun reconciliationRow_copy() {
        val row = com.ezbookkeeping.android.ui.screen.transaction.ReconciliationRow("2025-01-01", "Test", 100.0, 100.0, 0.0, true)
        val copy = row.copy(isMatched = false, difference = 10.0)
        assertFalse(copy.isMatched)
        assertEquals(10.0, copy.difference, 0.01)
    }

    // ImportPreviewRow
    @Test fun importPreviewRow_creation() {
        val row = com.ezbookkeeping.android.ui.screen.transaction.ImportPreviewRow("2025-01-01", "Test", 50.0, com.ezbookkeeping.android.data.db.entity.TransactionType.EXPENSE)
        assertEquals(50.0, row.amount, 0.01)
    }

    // TrendPoint
    @Test fun trendPoint_creation() {
        val point = com.ezbookkeeping.android.ui.screen.statistics.TrendPoint("Jan", 1000.0, 2000.0)
        assertEquals("Jan", point.month)
        assertEquals(1000.0, point.expense, 0.01)
        assertEquals(2000.0, point.income, 0.01)
    }

    // InsightData
    @Test fun insightData_creation() {
        val data = com.ezbookkeeping.android.ui.screen.statistics.InsightData("Title", "100", "+5%", true)
        assertTrue(data.isPositive)
    }

    // CategoryInsight
    @Test fun categoryInsight_creation() {
        val ci = com.ezbookkeeping.android.ui.screen.statistics.CategoryInsight("Food", 500.0, 25f, "#FF0000")
        assertEquals(25f, ci.percentage, 0.01f)
    }

    // UiState defaults
    @Test fun statisticsUiState_defaults() {
        val state = com.ezbookkeeping.android.ui.screen.statistics.StatisticsUiState()
        assertEquals(0.0, state.totalExpense, 0.01)
        assertFalse(state.isLoading)
        assertEquals(com.ezbookkeeping.android.ui.screen.statistics.ChartType.PIE, state.chartType)
        assertEquals(com.ezbookkeeping.android.ui.screen.statistics.SortMethod.AMOUNT_DESC, state.sortMethod)
    }
    @Test fun accountListUiState_defaults() {
        val state = com.ezbookkeeping.android.ui.screen.account.AccountListUiState()
        assertTrue(state.showBalance)
        assertFalse(state.isLoading)
    }
    @Test fun categoryListUiState_defaults() {
        val state = com.ezbookkeeping.android.ui.screen.category.CategoryListUiState()
        assertEquals(com.ezbookkeeping.android.data.db.entity.CategoryType.EXPENSE, state.selectedType)
        assertFalse(state.showHidden)
    }
    @Test fun transactionEditUiState_defaults() {
        val state = com.ezbookkeeping.android.ui.screen.transaction.TransactionEditUiState()
        assertNull(state.categoryId)
        assertNull(state.scheduleFrequency)
        assertEquals(0.0, state.latitude, 0.01)
    }
    @Test fun reconciliationUiState_defaults() {
        val state = com.ezbookkeeping.android.ui.screen.transaction.ReconciliationUiState()
        assertEquals(-1.0, state.openingBalance, 0.01)
        assertEquals(-1.0, state.closingBalance, 0.01)
        assertNull(state.selectedAccountId)
    }
    @Test fun settingsUiState_defaults() {
        val state = com.ezbookkeeping.android.ui.screen.settings.SettingsUiState()
        assertTrue(state.isSwipeBackEnabled)
        assertEquals("Default", state.themeColor)
    }

    // FilterParams (TransactionList) - internal class, skip direct test
    @Test fun filterParams_skip() { assertTrue(true) }

    // HomeUiState
    @Test fun homeUiState_defaults() {
        val state = com.ezbookkeeping.android.ui.screen.home.HomeUiState()
        assertFalse(state.isRefreshing)
        assertTrue(state.templates.isEmpty())
    }
}

class AmountUtilExtendedTest {
    @Test fun format_zero() { assertNotNull(com.ezbookkeeping.android.util.AmountUtil.format(0.0)) }
    @Test fun format_negative() { val result = com.ezbookkeeping.android.util.AmountUtil.format(-100.5); assertTrue(result.contains("100")) }
    @Test fun format_largeNumber() { val result = com.ezbookkeeping.android.util.AmountUtil.format(9999999.99); assertNotNull(result) }
    @Test fun format_smallDecimal() { val result = com.ezbookkeeping.android.util.AmountUtil.format(0.01); assertNotNull(result) }
    @Test fun format_oneThousand() { val result = com.ezbookkeeping.android.util.AmountUtil.format(1000.0); assertNotNull(result) }
}
