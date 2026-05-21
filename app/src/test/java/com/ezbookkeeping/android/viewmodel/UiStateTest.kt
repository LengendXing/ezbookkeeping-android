package com.ezbookkeeping.android.viewmodel

import com.ezbookkeeping.android.data.db.entity.*
import com.ezbookkeeping.android.ui.screen.account.AccountListUiState
import com.ezbookkeeping.android.ui.screen.category.CategoryListUiState
import com.ezbookkeeping.android.ui.screen.statistics.CategoryStat
import com.ezbookkeeping.android.ui.screen.statistics.DateRange
import com.ezbookkeeping.android.ui.screen.statistics.StatisticsUiState
import com.ezbookkeeping.android.ui.screen.template.TemplateListUiState
import com.ezbookkeeping.android.ui.screen.tag.TagListUiState
import com.ezbookkeeping.android.ui.screen.settings.ApplicationLockUiState
import org.junit.Assert.*
import org.junit.Test

class UiStateTest {

    // --- AccountListUiState ---
    @Test
    fun `AccountListUiState defaults`() {
        val state = AccountListUiState()
        assertTrue(state.accounts.isEmpty())
        assertFalse(state.isLoading)
        assertTrue(state.showBalance)
    }

    @Test
    fun `AccountListUiState copy preserves values`() {
        val state = AccountListUiState(accounts = listOf(), isLoading = true, showBalance = false)
        assertFalse(state.showBalance)
        assertTrue(state.isLoading)
    }

    @Test
    fun `AccountListUiState showBalance toggle`() {
        var state = AccountListUiState(showBalance = true)
        state = state.copy(showBalance = !state.showBalance)
        assertFalse(state.showBalance)
    }

    // --- CategoryListUiState ---
    @Test
    fun `CategoryListUiState defaults`() {
        val state = CategoryListUiState()
        assertTrue(state.categories.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(CategoryType.EXPENSE, state.selectedType)
    }

    @Test
    fun `CategoryListUiState type change`() {
        val state = CategoryListUiState(selectedType = CategoryType.INCOME)
        assertEquals(CategoryType.INCOME, state.selectedType)
    }

    @Test
    fun `CategoryListUiState all type values work`() {
        CategoryType.values().forEach { type ->
            val state = CategoryListUiState(selectedType = type)
            assertEquals(type, state.selectedType)
        }
    }

    // --- TagListUiState ---
    @Test
    fun `TagListUiState defaults`() {
        val state = TagListUiState()
        assertTrue(state.groups.isEmpty())
        assertTrue(state.tags.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `TagListUiState with data`() {
        val group = TagGroupEntity(1, 1, "Group1", 0)
        val tag = TagEntity(1, 1, 1, "Tag1")
        val state = TagListUiState(groups = listOf(group), tags = listOf(tag))
        assertEquals(1, state.groups.size)
        assertEquals(1, state.tags.size)
    }

    // --- TemplateListUiState ---
    @Test
    fun `TemplateListUiState defaults`() {
        val state = TemplateListUiState()
        assertTrue(state.templates.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `TemplateListUiState with template`() {
        val tmpl = TemplateEntity(1, 1, "Test", 100.0, TransactionType.EXPENSE, 1)
        val state = TemplateListUiState(templates = listOf(tmpl))
        assertEquals(1, state.templates.size)
        assertEquals("Test", state.templates[0].name)
    }

    // --- StatisticsUiState ---
    @Test
    fun `StatisticsUiState defaults`() {
        val state = StatisticsUiState()
        assertEquals(0.0, state.totalExpense, 0.01)
        assertEquals(0.0, state.totalIncome, 0.01)
        assertEquals(0.0, state.totalTransfer, 0.01)
        assertFalse(state.isLoading)
        assertEquals(DateRange.THIS_MONTH, state.dateRange)
    }

    @Test
    fun `StatisticsUiState date range change`() {
        val state = StatisticsUiState(dateRange = DateRange.LAST_MONTH)
        assertEquals(DateRange.LAST_MONTH, state.dateRange)
    }

    @Test
    fun `StatisticsUiState all DateRange values`() {
        DateRange.values().forEach { range ->
            val state = StatisticsUiState(dateRange = range)
            assertEquals(range, state.dateRange)
        }
    }

    // --- CategoryStat ---
    @Test
    fun `CategoryStat creation`() {
        val stat = CategoryStat(1, "Food", "#FF0000", 100.0, 50f)
        assertEquals(1, stat.categoryId)
        assertEquals("Food", stat.name)
        assertEquals(100.0, stat.amount, 0.01)
        assertEquals(50f, stat.percentage, 0.01f)
    }

    @Test
    fun `CategoryStat null categoryId`() {
        val stat = CategoryStat(null, "Unknown", "#000000", 50.0, 25f)
        assertNull(stat.categoryId)
    }

    @Test
    fun `CategoryStat zero percentage`() {
        val stat = CategoryStat(1, "Cat", "#000", 0.0, 0f)
        assertEquals(0f, stat.percentage, 0.01f)
    }

    @Test
    fun `CategoryStat 100 percent`() {
        val stat = CategoryStat(1, "Cat", "#000", 500.0, 100f)
        assertEquals(100f, stat.percentage, 0.01f)
    }

    // --- ApplicationLockUiState ---
    @Test
    fun `ApplicationLockUiState defaults`() {
        val state = ApplicationLockUiState()
        assertEquals(LockType.NONE, state.currentLockType)
        assertFalse(state.isSettingPin)
        assertFalse(state.isSettingPassword)
        assertEquals("", state.inputCode)
        assertEquals("", state.confirmCode)
        assertNull(state.error)
        assertFalse(state.isBiometricAvailable)
    }

    @Test
    fun `ApplicationLockUiState pin mode`() {
        val state = ApplicationLockUiState(isSettingPin = true, inputCode = "12")
        assertTrue(state.isSettingPin)
        assertEquals("12", state.inputCode)
    }

    @Test
    fun `ApplicationLockUiState password mode`() {
        val state = ApplicationLockUiState(isSettingPassword = true, inputCode = "pass")
        assertTrue(state.isSettingPassword)
    }

    @Test
    fun `ApplicationLockUiState error state`() {
        val state = ApplicationLockUiState(error = "PINs do not match")
        assertEquals("PINs do not match", state.error)
    }

    @Test
    fun `ApplicationLockUiState biometric available`() {
        val state = ApplicationLockUiState(isBiometricAvailable = true)
        assertTrue(state.isBiometricAvailable)
    }

    // --- DateRange ---
    @Test
    fun `DateRange labels`() {
        assertEquals("This Month", DateRange.THIS_MONTH.label)
        assertEquals("Last Month", DateRange.LAST_MONTH.label)
        assertEquals("This Year", DateRange.THIS_YEAR.label)
        assertEquals("All Time", DateRange.ALL.label)
    }

    @Test
    fun `DateRange has 4 values`() { assertEquals(4, DateRange.values().size) }

    // --- Entity data classes ---
    @Test
    fun `AccountEntity creation`() {
        val entity = AccountEntity(1, 1, AccountType.ASSET, "Cash", "wallet", "#4CAF50", "CNY", 1000.0)
        assertEquals(1, entity.id)
        assertEquals("Cash", entity.name)
        assertEquals(AccountType.ASSET, entity.type)
        assertEquals("CNY", entity.currency)
    }

    @Test
    fun `AccountEntity defaults`() {
        val entity = AccountEntity(1, 1, AccountType.LIABILITY, "Card", "card", "#F44336", "USD")
        assertEquals(0.0, entity.balance, 0.01)
        assertEquals(0.0, entity.creditLimit, 0.01)
        assertEquals(0.0, entity.initialBalance, 0.01)
        assertTrue(entity.isCounting)
        assertEquals(0, entity.order)
    }

    @Test
    fun `CategoryEntity creation with parentId`() {
        val entity = CategoryEntity(2, 1, CategoryType.EXPENSE, 1, "Coffee", "coffee", "#8B4513")
        assertEquals(1, entity.parentId)
    }

    @Test
    fun `CategoryEntity creation without parentId`() {
        val entity = CategoryEntity(1, 1, CategoryType.EXPENSE, null, "Food", "food", "#FF9800")
        assertNull(entity.parentId)
    }

    @Test
    fun `CategoryEntity isHidden default`() {
        val entity = CategoryEntity(1, 1, CategoryType.EXPENSE, null, "Cat", "cat", "#000")
        assertFalse(entity.isHidden)
    }

    @Test
    fun `TagEntity creation`() {
        val tag = TagEntity(1, 1, 1, "Tag1")
        assertEquals(1, tag.id)
        assertEquals(1, tag.groupId)
        assertEquals("Tag1", tag.name)
    }

    @Test
    fun `TagGroupEntity creation`() {
        val group = TagGroupEntity(1, 1, "Group1", 0)
        assertEquals("Group1", group.name)
        assertEquals(0, group.order)
    }

    @Test
    fun `TemplateEntity creation`() {
        val tmpl = TemplateEntity(1, 1, "Lunch", 50.0, TransactionType.EXPENSE, 1)
        assertEquals("Lunch", tmpl.name)
        assertEquals(50.0, tmpl.amount, 0.01)
        assertNull(tmpl.destinationAccountId)
        assertNull(tmpl.categoryId)
    }

    @Test
    fun `TemplateEntity transfer type`() {
        val tmpl = TemplateEntity(2, 1, "Transfer", 100.0, TransactionType.TRANSFER, 1, 2, 3)
        assertEquals(TransactionType.TRANSFER, tmpl.type)
        assertEquals(2, tmpl.destinationAccountId)
    }

    @Test
    fun `AccountType equality`() {
        assertEquals(AccountType.ASSET, AccountType.ASSET)
        assertNotEquals(AccountType.ASSET, AccountType.LIABILITY)
    }

    @Test
    fun `TransactionType equality`() {
        assertEquals(TransactionType.EXPENSE, TransactionType.EXPENSE)
        assertNotEquals(TransactionType.EXPENSE, TransactionType.INCOME)
    }

    @Test
    fun `LockType NONE is default`() {
        assertEquals(LockType.NONE, LockType.valueOf("NONE"))
    }

    @Test
    fun `CategoryType matches TransactionType names`() {
        CategoryType.values().forEach { ct ->
            assertNotNull(TransactionType.valueOf(ct.name))
        }
    }

    @Test
    fun `AccountEntity equality by value`() {
        val a = AccountEntity(1, 1, AccountType.ASSET, "Cash", "w", "#0", "CNY")
        val b = AccountEntity(1, 1, AccountType.ASSET, "Cash", "w", "#0", "CNY")
        assertEquals(a, b)
    }

    @Test
    fun `CategoryEntity different id not equal`() {
        val a = CategoryEntity(1, 1, CategoryType.EXPENSE, null, "A", "a", "#0")
        val b = CategoryEntity(2, 1, CategoryType.EXPENSE, null, "A", "a", "#0")
        assertNotEquals(a, b)
    }

    @Test
    fun `TagEntity equality`() {
        val a = TagEntity(1, 1, 1, "Tag")
        val b = TagEntity(1, 1, 1, "Tag")
        assertEquals(a, b)
    }

    @Test
    fun `TagGroupEntity equality`() {
        val a = TagGroupEntity(1, 1, "G", 0)
        val b = TagGroupEntity(1, 1, "G", 0)
        assertEquals(a, b)
    }

    @Test
    fun `TemplateEntity equality`() {
        val a = TemplateEntity(1, 1, "T", 10.0, TransactionType.EXPENSE, 1)
        val b = TemplateEntity(1, 1, "T", 10.0, TransactionType.EXPENSE, 1)
        assertEquals(a, b)
    }
}
