package com.ezbookkeeping.android.viewmodel

import com.ezbookkeeping.android.data.db.entity.*
import com.ezbookkeeping.android.ui.screen.transaction.*
import com.ezbookkeeping.android.ui.screen.statistics.*
import com.ezbookkeeping.android.ui.screen.settings.*
import org.junit.Assert.*
import org.junit.Test

class NewFeaturesTest {

    // ===== TransactionImportUiState =====

    @Test fun `TransactionImportUiState defaults`() {
        val state = TransactionImportUiState()
        assertFalse(state.isLoading)
        assertTrue(state.previewData.isEmpty())
        assertNull(state.importResult)
        assertFalse(state.isImporting)
        assertEquals(ImportFormat.CSV, state.selectedFormat)
    }

    @Test fun `TransactionImportUiState format change`() {
        val state = TransactionImportUiState(selectedFormat = ImportFormat.OFX)
        assertEquals(ImportFormat.OFX, state.selectedFormat)
    }

    @Test fun `TransactionImportUiState with preview data`() {
        val row = ImportPreviewRow("2025-01-15", "Grocery", 45.5, TransactionType.EXPENSE)
        val state = TransactionImportUiState(previewData = listOf(row))
        assertEquals(1, state.previewData.size)
        assertEquals("Grocery", state.previewData[0].comment)
    }

    @Test fun `TransactionImportUiState importing state`() {
        val state = TransactionImportUiState(isImporting = true)
        assertTrue(state.isImporting)
    }

    @Test fun `TransactionImportUiState result message`() {
        val state = TransactionImportUiState(importResult = "Successfully imported 3 transactions")
        assertNotNull(state.importResult)
    }

    @Test fun `ImportFormat has 3 values`() { assertEquals(3, ImportFormat.values().size) }

    @Test fun `ImportFormat CSV label`() { assertEquals("CSV", ImportFormat.CSV.label) }

    @Test fun `ImportFormat OFX label`() { assertEquals("OFX", ImportFormat.OFX.label) }

    @Test fun `ImportFormat QIF label`() { assertEquals("QIF", ImportFormat.QIF.label) }

    @Test fun `ImportPreviewRow expense type`() {
        val row = ImportPreviewRow("2025-01-01", "Test", 100.0, TransactionType.EXPENSE)
        assertEquals(TransactionType.EXPENSE, row.type)
    }

    @Test fun `ImportPreviewRow income type`() {
        val row = ImportPreviewRow("2025-01-01", "Salary", 5000.0, TransactionType.INCOME)
        assertEquals(TransactionType.INCOME, row.type)
    }

    @Test fun `ImportPreviewRow transfer type`() {
        val row = ImportPreviewRow("2025-01-01", "Transfer", 200.0, TransactionType.TRANSFER)
        assertEquals(TransactionType.TRANSFER, row.type)
    }

    @Test fun `ImportPreviewRow date format`() {
        val row = ImportPreviewRow("2025-12-31", "Test", 10.0, TransactionType.EXPENSE)
        assertTrue(row.date.contains("-"))
    }

    @Test fun `ImportPreviewRow negative amount possible`() {
        val row = ImportPreviewRow("2025-01-01", "Refund", -50.0, TransactionType.EXPENSE)
        assertTrue(row.amount < 0)
    }

    @Test fun `ImportPreviewRow zero amount`() {
        val row = ImportPreviewRow("2025-01-01", "Free", 0.0, TransactionType.INCOME)
        assertEquals(0.0, row.amount, 0.01)
    }

    // ===== CategoryPresetUiState =====

    @Test fun `CategoryPresetUiState defaults`() {
        val state = CategoryPresetUiState()
        assertFalse(state.isLoading)
        assertTrue(state.presets.isEmpty())
        assertTrue(state.selectedPresets.isEmpty())
        assertFalse(state.isImporting)
        assertNull(state.importResult)
    }

    @Test fun `CategoryPresetUiState with presets`() {
        val preset = CategoryPreset("daily_expense", "Daily Expenses", "Expense", listOf("Food", "Transport"))
        val state = CategoryPresetUiState(presets = listOf(preset))
        assertEquals(1, state.presets.size)
        assertEquals("Daily Expenses", state.presets[0].name)
    }

    @Test fun `CategoryPresetUiState selected presets`() {
        val state = CategoryPresetUiState(selectedPresets = setOf("id1", "id2"))
        assertEquals(2, state.selectedPresets.size)
    }

    @Test fun `CategoryPresetUiState importing`() {
        val state = CategoryPresetUiState(isImporting = true)
        assertTrue(state.isImporting)
    }

    @Test fun `CategoryPresetUiState result`() {
        val state = CategoryPresetUiState(importResult = "Imported 2 presets")
        assertEquals("Imported 2 presets", state.importResult)
    }

    @Test fun `CategoryPreset subCategories count`() {
        val preset = CategoryPreset("travel", "Travel", "Expense", listOf("Flights", "Hotel", "Food"))
        assertEquals(3, preset.subCategories.size)
    }

    @Test fun `CategoryPreset id uniqueness`() {
        val p1 = CategoryPreset("a", "A", "Expense", emptyList())
        val p2 = CategoryPreset("b", "B", "Income", emptyList())
        assertNotEquals(p1.id, p2.id)
    }

    // ===== ExchangeRateUpdateUiState =====

    @Test fun `ExchangeRateUpdateUiState defaults`() {
        val state = ExchangeRateUpdateUiState()
        assertFalse(state.isLoading)
        assertFalse(state.isAutoUpdate)
        assertEquals(UpdateFrequency.DAILY, state.updateFrequency)
        assertNull(state.lastUpdated)
        assertFalse(state.isUpdating)
        assertNull(state.updateResult)
        assertEquals("ecb", state.selectedSource)
    }

    @Test fun `ExchangeRateUpdateUiState auto update on`() {
        val state = ExchangeRateUpdateUiState(isAutoUpdate = true)
        assertTrue(state.isAutoUpdate)
    }

    @Test fun `ExchangeRateUpdateUiState weekly frequency`() {
        val state = ExchangeRateUpdateUiState(updateFrequency = UpdateFrequency.WEEKLY)
        assertEquals(UpdateFrequency.WEEKLY, state.updateFrequency)
    }

    @Test fun `ExchangeRateUpdateUiState monthly frequency`() {
        val state = ExchangeRateUpdateUiState(updateFrequency = UpdateFrequency.MONTHLY)
        assertEquals(UpdateFrequency.MONTHLY, state.updateFrequency)
    }

    @Test fun `UpdateFrequency has 3 values`() { assertEquals(3, UpdateFrequency.values().size) }

    @Test fun `UpdateFrequency labels`() {
        assertEquals("Daily", UpdateFrequency.DAILY.label)
        assertEquals("Weekly", UpdateFrequency.WEEKLY.label)
        assertEquals("Monthly", UpdateFrequency.MONTHLY.label)
    }

    @Test fun `RateSource creation`() {
        val src = RateSource("ecb", "European Central Bank")
        assertEquals("ecb", src.id)
        assertEquals("European Central Bank", src.name)
    }

    @Test fun `ExchangeRateUpdateUiState available sources`() {
        val state = ExchangeRateUpdateUiState()
        assertEquals(3, state.availableSources.size)
    }

    @Test fun `ExchangeRateUpdateUiState updating`() {
        val state = ExchangeRateUpdateUiState(isUpdating = true)
        assertTrue(state.isUpdating)
    }

    // ===== ReconciliationUiState =====

    @Test fun `ReconciliationUiState defaults`() {
        val state = ReconciliationUiState()
        assertFalse(state.isLoading)
        assertEquals(0.0, state.statementBalance, 0.01)
        assertEquals(0.0, state.bookBalance, 0.01)
        assertEquals(0.0, state.difference, 0.01)
        assertTrue(state.rows.isEmpty())
    }

    @Test fun `ReconciliationRow matched`() {
        val row = ReconciliationRow("2025-01-01", "Test", 100.0, 100.0, 0.0, true)
        assertTrue(row.isMatched)
        assertEquals(0.0, row.difference, 0.01)
    }

    @Test fun `ReconciliationRow unmatched`() {
        val row = ReconciliationRow("2025-01-01", "Test", 100.0, 110.0, 10.0, false)
        assertFalse(row.isMatched)
        assertTrue(row.difference > 0)
    }

    @Test fun `ReconciliationUiState positive difference`() {
        val state = ReconciliationUiState(statementBalance = 15000.0, bookBalance = 13350.0, difference = 1650.0)
        assertTrue(state.difference > 0)
    }

    @Test fun `ReconciliationUiState zero difference`() {
        val state = ReconciliationUiState(statementBalance = 1000.0, bookBalance = 1000.0, difference = 0.0)
        assertEquals(0.0, state.difference, 0.01)
    }

    @Test fun `ReconciliationUiState negative difference`() {
        val state = ReconciliationUiState(statementBalance = 1000.0, bookBalance = 1200.0, difference = -200.0)
        assertTrue(state.difference < 0)
    }

    @Test fun `ReconciliationUiState reconciling`() {
        val state = ReconciliationUiState(isReconciling = true)
        assertTrue(state.isReconciling)
    }

    @Test fun `ReconciliationUiState result`() {
        val state = ReconciliationUiState(result = "All items matched")
        assertEquals("All items matched", state.result)
    }

    @Test fun `ReconciliationRow negative amounts`() {
        val row = ReconciliationRow("2025-01-01", "Payment", -200.0, -210.0, -10.0, false)
        assertTrue(row.statementAmount < 0)
        assertTrue(row.bookAmount < 0)
    }

    // ===== InsightExplorerUiState =====

    @Test fun `InsightExplorerUiState defaults`() {
        val state = InsightExplorerUiState()
        assertFalse(state.isLoading)
        assertEquals(InsightPeriod.MONTHLY, state.selectedPeriod)
        assertTrue(state.insights.isEmpty())
        assertTrue(state.trendData.isEmpty())
        assertTrue(state.topExpenseCategories.isEmpty())
        assertEquals(0f, state.savingsRate)
    }

    @Test fun `InsightExplorerUiState period change`() {
        val state = InsightExplorerUiState(selectedPeriod = InsightPeriod.YEARLY)
        assertEquals(InsightPeriod.YEARLY, state.selectedPeriod)
    }

    @Test fun `InsightPeriod has 3 values`() { assertEquals(3, InsightPeriod.values().size) }

    @Test fun `InsightPeriod labels`() {
        assertEquals("Weekly", InsightPeriod.WEEKLY.label)
        assertEquals("Monthly", InsightPeriod.MONTHLY.label)
        assertEquals("Yearly", InsightPeriod.YEARLY.label)
    }

    @Test fun `TrendPoint creation`() {
        val point = TrendPoint("Jan", 2500.0, 5000.0)
        assertEquals("Jan", point.month)
        assertEquals(2500.0, point.expense, 0.01)
        assertEquals(5000.0, point.income, 0.01)
    }

    @Test fun `InsightData positive trend`() {
        val insight = InsightData("Savings", "2,650", "+12.5%", true)
        assertTrue(insight.isPositive)
    }

    @Test fun `InsightData negative trend`() {
        val insight = InsightData("Expenses", "2,350", "-8.2%", false)
        assertFalse(insight.isPositive)
    }

    @Test fun `CategoryInsight creation`() {
        val insight = CategoryInsight("Food", 850.0, 36.2f, "#FF6B6B")
        assertEquals("Food", insight.name)
        assertEquals(850.0, insight.amount, 0.01)
        assertEquals(36.2f, insight.percentage, 0.1f)
    }

    @Test fun `CategoryInsight percentages sum approximately 100`() {
        val cats = listOf(
            CategoryInsight("A", 100.0, 36.2f, "#0"),
            CategoryInsight("B", 80.0, 29.8f, "#0"),
            CategoryInsight("C", 50.0, 14.9f, "#0"),
            CategoryInsight("D", 40.0, 11.9f, "#0"),
            CategoryInsight("E", 20.0, 7.2f, "#0")
        )
        val total = cats.sumOf { it.percentage.toDouble() }.toFloat()
        assertTrue(total in 99f..101f)
    }

    @Test fun `InsightExplorerUiState savings rate`() {
        val state = InsightExplorerUiState(savingsRate = 53.0f)
        assertEquals(53.0f, state.savingsRate, 0.1f)
    }

    // ===== PageSettingsUiState =====

    @Test fun `PageSettingsUiState defaults`() {
        val state = PageSettingsUiState()
        assertEquals("Home", state.defaultLandingPage)
        assertTrue(state.showOverviewCard)
        assertTrue(state.showTransactionAmount)
        assertTrue(state.showTransactionComment)
        assertEquals(50, state.transactionPageSize)
    }

    @Test fun `PageSettingsUiState landing page change`() {
        val state = PageSettingsUiState(defaultLandingPage = "Statistics")
        assertEquals("Statistics", state.defaultLandingPage)
    }

    @Test fun `PageSettingsUiState overview card off`() {
        val state = PageSettingsUiState(showOverviewCard = false)
        assertFalse(state.showOverviewCard)
    }

    @Test fun `PageSettingsUiState page size options`() {
        val state = PageSettingsUiState(transactionPageSize = 100)
        assertEquals(100, state.transactionPageSize)
    }

    @Test fun `PageSettingsUiState available pages`() {
        val state = PageSettingsUiState()
        assertEquals(4, state.availablePages.size)
    }

    // ===== TextSizeSettingsUiState =====

    @Test fun `TextSizeSettingsUiState defaults`() {
        val state = TextSizeSettingsUiState()
        assertEquals(1.0f, state.textSizeScale, 0.01f)
    }

    @Test fun `TextSizeSettingsUiState large text`() {
        val state = TextSizeSettingsUiState(textSizeScale = 1.5f)
        assertEquals(1.5f, state.textSizeScale, 0.01f)
    }

    @Test fun `TextSizeSettingsUiState small text`() {
        val state = TextSizeSettingsUiState(textSizeScale = 0.8f)
        assertEquals(0.8f, state.textSizeScale, 0.01f)
    }

    // ===== AccountFilterSettingsUiState =====

    @Test fun `AccountFilterSettingsUiState defaults`() {
        val state = AccountFilterSettingsUiState()
        assertTrue(state.availableAccounts.isEmpty())
        assertTrue(state.selectAll)
    }

    @Test fun `FilterItem selected`() {
        val item = FilterItem(1, "Cash", true)
        assertTrue(item.isSelected)
    }

    @Test fun `FilterItem unselected`() {
        val item = FilterItem(2, "Card", false)
        assertFalse(item.isSelected)
    }

    @Test fun `AccountFilterSettingsUiState with mixed selection`() {
        val state = AccountFilterSettingsUiState(availableAccounts = listOf(FilterItem(1, "Cash", true), FilterItem(2, "Card", false)), selectAll = false)
        assertEquals(2, state.availableAccounts.size)
        assertFalse(state.selectAll)
    }

    @Test fun `AccountFilterSettingsUiState all selected`() {
        val state = AccountFilterSettingsUiState(availableAccounts = listOf(FilterItem(1, "Cash", true), FilterItem(2, "Card", true)), selectAll = true)
        assertTrue(state.selectAll)
    }

    @Test fun `FilterItem toggle logic`() {
        val item = FilterItem(1, "Cash", true)
        val toggled = item.copy(isSelected = !item.isSelected)
        assertFalse(toggled.isSelected)
    }

    // ===== CategoryFilterSettingsUiState =====

    @Test fun `CategoryFilterSettingsUiState defaults`() {
        val state = CategoryFilterSettingsUiState()
        assertTrue(state.expenseCategories.isEmpty())
        assertTrue(state.incomeCategories.isEmpty())
        assertTrue(state.selectAll)
    }

    @Test fun `CategoryFilterSettingsUiState with data`() {
        val state = CategoryFilterSettingsUiState(
            expenseCategories = listOf(FilterItem(1, "Food", true)),
            incomeCategories = listOf(FilterItem(2, "Salary", true))
        )
        assertEquals(1, state.expenseCategories.size)
        assertEquals(1, state.incomeCategories.size)
    }

    // ===== TransactionTagFilterSettingsUiState =====

    @Test fun `TransactionTagFilterSettingsUiState defaults`() {
        val state = TransactionTagFilterSettingsUiState()
        assertTrue(state.availableTags.isEmpty())
        assertTrue(state.selectAll)
    }

    @Test fun `TransactionTagFilterSettingsUiState with mixed selection`() {
        val state = TransactionTagFilterSettingsUiState(availableTags = listOf(FilterItem(1, "Work", true), FilterItem(2, "Personal", false)), selectAll = false)
        assertEquals(2, state.availableTags.size)
        assertFalse(state.selectAll)
    }

    @Test fun `TransactionTagFilterSettingsUiState all selected`() {
        val state = TransactionTagFilterSettingsUiState(availableTags = listOf(FilterItem(1, "Work", true), FilterItem(2, "Personal", true)), selectAll = true)
        assertTrue(state.selectAll)
    }

    // ===== DisplayOrderSettingsUiState =====

    @Test fun `DisplayOrderSettingsUiState defaults`() {
        val state = DisplayOrderSettingsUiState()
        assertTrue(state.accountOrder.isEmpty())
        assertTrue(state.categoryOrder.isEmpty())
        assertEquals(DisplayTab.ACCOUNTS, state.activeTab)
    }

    @Test fun `DisplayOrderItem creation`() {
        val item = DisplayOrderItem(1, "Cash", 1)
        assertEquals("Cash", item.name)
        assertEquals(1, item.order)
    }

    @Test fun `DisplayTab has 2 values`() { assertEquals(2, DisplayTab.values().size) }

    @Test fun `DisplayTab labels`() {
        assertEquals("Accounts", DisplayTab.ACCOUNTS.label)
        assertEquals("Categories", DisplayTab.CATEGORIES.label)
    }

    @Test fun `DisplayOrderSettingsUiState tab change`() {
        val state = DisplayOrderSettingsUiState(activeTab = DisplayTab.CATEGORIES)
        assertEquals(DisplayTab.CATEGORIES, state.activeTab)
    }

    // ===== CloudSyncSettingsUiState =====

    @Test fun `CloudSyncSettingsUiState defaults`() {
        val state = CloudSyncSettingsUiState()
        assertFalse(state.isSyncEnabled)
        assertEquals(SyncProvider.NONE, state.syncProvider)
        assertNull(state.lastSyncTime)
        assertFalse(state.isSyncing)
        assertNull(state.syncResult)
        assertFalse(state.autoSync)
        assertTrue(state.wifiOnly)
    }

    @Test fun `SyncProvider has 4 values`() { assertEquals(4, SyncProvider.values().size) }

    @Test fun `SyncProvider labels`() {
        assertEquals("None", SyncProvider.NONE.label)
        assertEquals("WebDAV", SyncProvider.WEBDAV.label)
        assertEquals("Dropbox", SyncProvider.DROPBOX.label)
        assertEquals("Google Drive", SyncProvider.GOOGLE_DRIVE.label)
    }

    @Test fun `CloudSyncSettingsUiState webdav provider`() {
        val state = CloudSyncSettingsUiState(syncProvider = SyncProvider.WEBDAV)
        assertEquals(SyncProvider.WEBDAV, state.syncProvider)
    }

    @Test fun `CloudSyncSettingsUiState auto sync on`() {
        val state = CloudSyncSettingsUiState(autoSync = true)
        assertTrue(state.autoSync)
    }

    @Test fun `CloudSyncSettingsUiState wifi only off`() {
        val state = CloudSyncSettingsUiState(wifiOnly = false)
        assertFalse(state.wifiOnly)
    }

    @Test fun `CloudSyncSettingsUiState syncing`() {
        val state = CloudSyncSettingsUiState(isSyncing = true)
        assertTrue(state.isSyncing)
    }

    @Test fun `CloudSyncSettingsUiState last sync time`() {
        val state = CloudSyncSettingsUiState(lastSyncTime = "2025-05-21 10:00")
        assertNotNull(state.lastSyncTime)
    }

    @Test fun `CloudSyncSettingsUiState sync result`() {
        val state = CloudSyncSettingsUiState(syncResult = "Sync completed")
        assertEquals("Sync completed", state.syncResult)
    }

    // ===== ImportFormat round-trip =====

    @Test fun `ImportFormat valueOf round trip`() {
        ImportFormat.values().forEach { assertEquals(it, ImportFormat.valueOf(it.name)) }
    }

    // ===== UpdateFrequency round-trip =====

    @Test fun `UpdateFrequency valueOf round trip`() {
        UpdateFrequency.values().forEach { assertEquals(it, UpdateFrequency.valueOf(it.name)) }
    }

    // ===== InsightPeriod round-trip =====

    @Test fun `InsightPeriod valueOf round trip`() {
        InsightPeriod.values().forEach { assertEquals(it, InsightPeriod.valueOf(it.name)) }
    }

    // ===== DisplayTab round-trip =====

    @Test fun `DisplayTab valueOf round trip`() {
        DisplayTab.values().forEach { assertEquals(it, DisplayTab.valueOf(it.name)) }
    }

    // ===== SyncProvider round-trip =====

    @Test fun `SyncProvider valueOf round trip`() {
        SyncProvider.values().forEach { assertEquals(it, SyncProvider.valueOf(it.name)) }
    }
}
