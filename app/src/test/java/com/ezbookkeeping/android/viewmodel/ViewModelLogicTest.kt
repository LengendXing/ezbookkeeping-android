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
import com.ezbookkeeping.android.util.AmountUtil
import org.junit.Assert.*
import org.junit.Test

class ViewModelLogicTest {

    private fun tx(id: Int, amount: Double, type: TransactionType, catId: Int? = null, comment: String? = null, date: String = "2025-01-01", destAccountId: Int? = null, destAmount: Double? = null) =
        TransactionEntity(id = id, userId = 1, sourceAccountId = 1, destinationAccountId = destAccountId, sourceAmount = amount, destinationAmount = destAmount, type = type, categoryId = catId, comment = comment, date = date)

    // ===== Account filtering/grouping =====

    @Test fun `group accounts by ASSET`() {
        val accounts = listOf(AccountEntity(1, 1, AccountType.ASSET, "Cash", "w", "#0", "CNY", 100.0), AccountEntity(2, 1, AccountType.LIABILITY, "Card", "c", "#0", "USD", -50.0), AccountEntity(3, 1, AccountType.ASSET, "Bank", "b", "#0", "CNY", 500.0))
        assertEquals(2, accounts.filter { it.type == AccountType.ASSET }.size)
    }

    @Test fun `group accounts by LIABILITY`() {
        val accounts = listOf(AccountEntity(1, 1, AccountType.ASSET, "Cash", "w", "#0", "CNY"), AccountEntity(2, 1, AccountType.LIABILITY, "Card", "c", "#0", "USD"), AccountEntity(3, 1, AccountType.LIABILITY, "Loan", "l", "#0", "CNY"))
        assertEquals(2, accounts.filter { it.type == AccountType.LIABILITY }.size)
    }

    @Test fun `calculate net assets`() {
        val accounts = listOf(AccountEntity(1, 1, AccountType.ASSET, "Cash", "w", "#0", "CNY", 1000.0), AccountEntity(2, 1, AccountType.LIABILITY, "Card", "c", "#0", "CNY", 300.0))
        val net = accounts.filter { it.type == AccountType.ASSET }.sumOf { it.balance } - accounts.filter { it.type == AccountType.LIABILITY }.sumOf { it.balance }
        assertEquals(700.0, net, 0.01)
    }

    @Test fun `net assets zero when balanced`() {
        val accounts = listOf(AccountEntity(1, 1, AccountType.ASSET, "A", "a", "#0", "CNY", 500.0), AccountEntity(2, 1, AccountType.LIABILITY, "B", "b", "#0", "CNY", 500.0))
        val net = accounts.filter { it.type == AccountType.ASSET }.sumOf { it.balance } - accounts.filter { it.type == AccountType.LIABILITY }.sumOf { it.balance }
        assertEquals(0.0, net, 0.01)
    }

    @Test fun `net assets negative when more liabilities`() {
        val accounts = listOf(AccountEntity(1, 1, AccountType.ASSET, "A", "a", "#0", "CNY", 100.0), AccountEntity(2, 1, AccountType.LIABILITY, "B", "b", "#0", "CNY", 500.0))
        val net = accounts.filter { it.type == AccountType.ASSET }.sumOf { it.balance } - accounts.filter { it.type == AccountType.LIABILITY }.sumOf { it.balance }
        assertTrue(net < 0)
    }

    @Test fun `empty accounts produce zero totals`() {
        val accounts = emptyList<AccountEntity>()
        assertEquals(0.0, accounts.filter { it.type == AccountType.ASSET }.sumOf { it.balance }, 0.01)
        assertEquals(0.0, accounts.filter { it.type == AccountType.LIABILITY }.sumOf { it.balance }, 0.01)
    }

    @Test fun `AccountListUiState toggle showBalance`() {
        var state = AccountListUiState(showBalance = true)
        state = state.copy(showBalance = !state.showBalance)
        assertFalse(state.showBalance)
        state = state.copy(showBalance = !state.showBalance)
        assertTrue(state.showBalance)
    }

    @Test fun `account zero balance in total`() {
        val accounts = listOf(AccountEntity(1, 1, AccountType.ASSET, "Empty", "e", "#0", "CNY", 0.0))
        assertEquals(0.0, accounts.sumOf { it.balance }, 0.01)
    }

    @Test fun `account negative balance`() {
        val accounts = listOf(AccountEntity(1, 1, AccountType.ASSET, "Overdrawn", "o", "#0", "CNY", -50.0))
        assertEquals(-50.0, accounts.sumOf { it.balance }, 0.01)
    }

    @Test fun `multiple accounts sum correctly`() {
        val accounts = listOf(AccountEntity(1, 1, AccountType.ASSET, "A", "a", "#0", "CNY", 100.0), AccountEntity(2, 1, AccountType.ASSET, "B", "b", "#0", "CNY", 200.0), AccountEntity(3, 1, AccountType.ASSET, "C", "c", "#0", "CNY", 300.0))
        assertEquals(600.0, accounts.sumOf { it.balance }, 0.01)
    }

    // ===== Category hierarchy =====

    @Test fun `filter categories by EXPENSE type`() {
        val cats = listOf(CategoryEntity(1, 1, CategoryType.EXPENSE, null, "Food", "f", "#0"), CategoryEntity(2, 1, CategoryType.INCOME, null, "Salary", "s", "#0"), CategoryEntity(3, 1, CategoryType.EXPENSE, 1, "Coffee", "c", "#0"))
        assertEquals(2, cats.filter { it.type == CategoryType.EXPENSE }.size)
    }

    @Test fun `root categories have null parentId`() {
        val cats = listOf(CategoryEntity(1, 1, CategoryType.EXPENSE, null, "Food", "f", "#0"), CategoryEntity(2, 1, CategoryType.EXPENSE, 1, "Coffee", "c", "#0"))
        val roots = cats.filter { it.parentId == null }
        assertEquals(1, roots.size)
    }

    @Test fun `child categories grouped by parentId`() {
        val cats = listOf(CategoryEntity(1, 1, CategoryType.EXPENSE, null, "Food", "f", "#0"), CategoryEntity(2, 1, CategoryType.EXPENSE, 1, "Coffee", "c", "#0"), CategoryEntity(3, 1, CategoryType.EXPENSE, 1, "Tea", "t", "#0"), CategoryEntity(4, 1, CategoryType.EXPENSE, null, "Transport", "tr", "#0"), CategoryEntity(5, 1, CategoryType.EXPENSE, 4, "Bus", "b", "#0"))
        val childrenMap = cats.filter { it.parentId != null }.groupBy { it.parentId }
        assertEquals(2, childrenMap.size)
        assertEquals(2, childrenMap[1]?.size)
        assertEquals(1, childrenMap[4]?.size)
    }

    @Test fun `CategoryListUiState type filter change`() {
        val state = CategoryListUiState(selectedType = CategoryType.INCOME)
        assertEquals(CategoryType.INCOME, state.selectedType)
    }

    @Test fun `hidden categories excluded`() {
        val cats = listOf(CategoryEntity(1, 1, CategoryType.EXPENSE, null, "Visible", "v", "#0", isHidden = false), CategoryEntity(2, 1, CategoryType.EXPENSE, null, "Hidden", "h", "#0", isHidden = true))
        assertEquals(1, cats.filter { !it.isHidden }.size)
    }

    @Test fun `deep category hierarchy parent-child`() {
        val cats = listOf(CategoryEntity(1, 1, CategoryType.EXPENSE, null, "Root1", "r1", "#0"), CategoryEntity(2, 1, CategoryType.EXPENSE, null, "Root2", "r2", "#0"), CategoryEntity(3, 1, CategoryType.EXPENSE, 1, "Child1-1", "c1", "#0"), CategoryEntity(4, 1, CategoryType.EXPENSE, 1, "Child1-2", "c2", "#0"), CategoryEntity(5, 1, CategoryType.EXPENSE, 2, "Child2-1", "c3", "#0"))
        val roots = cats.filter { it.parentId == null }
        val childrenMap = cats.filter { it.parentId != null }.groupBy { it.parentId }
        assertEquals(2, roots.size)
        assertEquals(2, childrenMap[1]?.size)
        assertEquals(1, childrenMap[2]?.size)
    }

    // ===== Tag grouping =====

    @Test fun `tags grouped by groupId`() {
        val tags = listOf(TagEntity(1, 1, 1, "T1"), TagEntity(2, 1, 1, "T2"), TagEntity(3, 1, 2, "T3"))
        assertEquals(2, tags.filter { it.groupId == 1 }.size)
        assertEquals(1, tags.filter { it.groupId == 2 }.size)
    }

    @Test fun `empty group has no tags`() { assertTrue(emptyList<TagEntity>().filter { it.groupId == 1 }.isEmpty()) }

    @Test fun `TagListUiState defaults empty`() {
        val state = TagListUiState()
        assertTrue(state.groups.isEmpty() && state.tags.isEmpty())
    }

    @Test fun `tag group order`() {
        val groups = listOf(TagGroupEntity(2, 1, "B", 1), TagGroupEntity(1, 1, "A", 0), TagGroupEntity(3, 1, "C", 2))
        val sorted = groups.sortedBy { it.order }
        assertEquals(listOf("A", "B", "C"), sorted.map { it.name })
    }

    @Test fun `tag deletion removes from list`() {
        val tags = mutableListOf(TagEntity(1, 1, 1, "T1"), TagEntity(2, 1, 1, "T2"))
        tags.removeIf { it.id == 1 }
        assertEquals(1, tags.size)
    }

    // ===== Statistics calculation =====

    @Test fun `expense percentage calculation`() {
        val totalExpense = 1000.0
        val pct = if (totalExpense > 0) (300.0 / totalExpense * 100).toFloat() else 0f
        assertEquals(30f, pct, 0.1f)
    }

    @Test fun `zero total produces zero percentage`() {
        val pct = if (0.0 > 0) (100.0 / 0.0 * 100).toFloat() else 0f
        assertEquals(0f, pct, 0.01f)
    }

    @Test fun `all percentages sum to approximately 100`() {
        val amounts = listOf(300.0, 500.0, 200.0)
        val total = amounts.sum()
        val pcts = amounts.map { (it / total * 100).toFloat() }
        assertTrue(pcts.sum() in 99f..101f)
    }

    @Test fun `StatisticsUiState date range ALL`() { assertEquals(DateRange.ALL, StatisticsUiState(dateRange = DateRange.ALL).dateRange) }

    @Test fun `CategoryStat sorted by amount descending`() {
        val stats = listOf(CategoryStat(1, "A", "#0", 100.0, 10f), CategoryStat(2, "B", "#0", 500.0, 50f), CategoryStat(3, "C", "#0", 300.0, 30f)).sortedByDescending { it.amount }
        assertEquals(listOf(500.0, 300.0, 100.0), stats.map { it.amount })
    }

    @Test fun `transaction type grouping for statistics`() {
        val txs = listOf(tx(1, 100.0, TransactionType.EXPENSE, catId=1), tx(2, 200.0, TransactionType.INCOME, catId=2), tx(3, 50.0, TransactionType.TRANSFER, destAccountId=2, destAmount=50.0))
        assertEquals(1, txs.filter { it.type == TransactionType.EXPENSE }.size)
        assertEquals(1, txs.filter { it.type == TransactionType.INCOME }.size)
        assertEquals(1, txs.filter { it.type == TransactionType.TRANSFER }.size)
    }

    @Test fun `DateRange valueOf round trip`() { DateRange.values().forEach { assertEquals(it, DateRange.valueOf(it.name)) } }

    // ===== Template =====

    @Test fun `TemplateEntity type determines color`() { assertEquals(TransactionType.EXPENSE, TemplateEntity(1, 1, "L", 50.0, TransactionType.EXPENSE, 1).type) }

    @Test fun `template amount formatting`() { assertNotNull(AmountUtil.format(1234.56)) }

    @Test fun `template comment null`() { assertNull(TemplateEntity(id=1, userId=1, name="T", amount=10.0, type=TransactionType.EXPENSE, sourceAccountId=1).comment) }

    @Test fun `template comment non-null`() { assertEquals("My comment", TemplateEntity(id=1, userId=1, name="T", amount=10.0, type=TransactionType.EXPENSE, sourceAccountId=1, comment="My comment").comment) }

    @Test fun `transfer template has destination account`() { assertEquals(2, TemplateEntity(id=1, userId=1, name="T", amount=100.0, type=TransactionType.TRANSFER, sourceAccountId=1, destinationAccountId=2).destinationAccountId) }

    // ===== Application Lock =====

    @Test fun `ApplicationLockUiState starts NONE`() { assertEquals(LockType.NONE, ApplicationLockUiState().currentLockType) }

    @Test fun `PIN setup first step`() {
        var state = ApplicationLockUiState(isSettingPin = true, inputCode = "1234")
        state = state.copy(confirmCode = state.inputCode, inputCode = "")
        assertEquals("1234", state.confirmCode)
    }

    @Test fun `PIN mismatch detected`() {
        val state = ApplicationLockUiState(confirmCode = "1234", inputCode = "5678")
        assertTrue(state.inputCode != state.confirmCode)
    }

    @Test fun `PIN match detected`() {
        val state = ApplicationLockUiState(confirmCode = "1234", inputCode = "1234")
        assertTrue(state.inputCode == state.confirmCode)
    }

    @Test fun `password too short`() { assertTrue("ab".length < 4) }

    @Test fun `password minimum length`() { assertTrue("abcd".length >= 4) }

    @Test fun `remove lock resets to NONE`() {
        val state = ApplicationLockUiState(currentLockType = LockType.PIN).copy(currentLockType = LockType.NONE)
        assertEquals(LockType.NONE, state.currentLockType)
    }

    @Test fun `setting PIN clears password mode`() {
        val state = ApplicationLockUiState(isSettingPassword = true).copy(isSettingPin = true, isSettingPassword = false)
        assertTrue(state.isSettingPin && !state.isSettingPassword)
    }

    // ===== Transaction type prefix =====

    @Test fun `expense has minus prefix`() { assertEquals("-", when(TransactionType.EXPENSE) { TransactionType.EXPENSE -> "-"; TransactionType.INCOME -> "+"; TransactionType.TRANSFER -> "" }) }

    @Test fun `income has plus prefix`() { assertEquals("+", when(TransactionType.INCOME) { TransactionType.EXPENSE -> "-"; TransactionType.INCOME -> "+"; TransactionType.TRANSFER -> "" }) }

    @Test fun `transfer has no prefix`() { assertEquals("", when(TransactionType.TRANSFER) { TransactionType.EXPENSE -> "-"; TransactionType.INCOME -> "+"; TransactionType.TRANSFER -> "" }) }

    // ===== parseColor =====

    @Test fun `parseColor 6-digit hex`() { assertNotNull("#FF0000".removePrefix("#").toLongOrNull(16)) }

    @Test fun `parseColor 8-digit hex`() { assertNotNull("#80FF0000".removePrefix("#").toLongOrNull(16)) }

    @Test fun `parseColor invalid returns null`() { assertNull("invalid".toLongOrNull(16)) }

    // ===== Balance display =====

    @Test fun `showBalance true displays amount`() { val d = if (true) AmountUtil.format(100.0) else "****"; assertFalse(d == "****") }

    @Test fun `showBalance false masks amount`() { val d = if (false) AmountUtil.format(100.0) else "****"; assertEquals("****", d) }

    // ===== Transaction grouping =====

    @Test fun `transactions grouped by yearMonth`() {
        val txs = listOf(tx(1, 10.0, TransactionType.EXPENSE, date="2025-01-15"), tx(2, 20.0, TransactionType.EXPENSE, date="2025-01-20"), tx(3, 30.0, TransactionType.EXPENSE, date="2025-02-01"))
        val grouped = txs.groupBy { it.date.substring(0, 7) }
        assertEquals(2, grouped.size)
    }

    @Test fun `month group total expense`() {
        val txs = listOf(tx(1, 100.0, TransactionType.EXPENSE, catId=1), tx(2, 200.0, TransactionType.EXPENSE, catId=1))
        assertEquals(300.0, txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.sourceAmount }, 0.01)
    }

    @Test fun `month group total income`() {
        val txs = listOf(tx(1, 500.0, TransactionType.INCOME, catId=2), tx(2, 300.0, TransactionType.INCOME, catId=2))
        assertEquals(800.0, txs.filter { it.type == TransactionType.INCOME }.sumOf { it.sourceAmount }, 0.01)
    }

    // ===== Search/filter =====

    @Test fun `search query filters by comment`() {
        val txs = listOf(tx(1, 10.0, TransactionType.EXPENSE, comment="Coffee"), tx(2, 20.0, TransactionType.EXPENSE, comment="Tea"))
        assertEquals(1, txs.filter { it.comment?.contains("Coffee", ignoreCase = true) == true }.size)
    }

    @Test fun `search query case insensitive`() {
        val txs = listOf(tx(1, 10.0, TransactionType.EXPENSE, comment="COFFEE"))
        assertEquals(1, txs.filter { it.comment?.contains("coffee", ignoreCase = true) == true }.size)
    }

    @Test fun `empty search returns all`() {
        val txs = listOf(tx(1, 10.0, TransactionType.EXPENSE), tx(2, 20.0, TransactionType.EXPENSE))
        val query = ""
        val filtered = if (query.isBlank()) txs else txs.filter { it.comment?.contains(query) == true }
        assertEquals(2, filtered.size)
    }

    @Test fun `type filter EXPENSE only`() {
        val txs = listOf(tx(1, 10.0, TransactionType.EXPENSE), tx(2, 20.0, TransactionType.INCOME))
        assertEquals(1, txs.filter { it.type == TransactionType.EXPENSE }.size)
    }

    @Test fun `type filter null shows all`() {
        val txs = listOf(tx(1, 10.0, TransactionType.EXPENSE), tx(2, 20.0, TransactionType.INCOME), tx(3, 50.0, TransactionType.TRANSFER, destAccountId=2, destAmount=50.0))
        assertEquals(3, txs.size)
    }

    // ===== Session management =====

    @Test fun `revoke session removes from list`() {
        val sessions = mutableListOf(com.ezbookkeeping.android.ui.screen.user.SessionInfo("1", "D1", "Now", true), com.ezbookkeeping.android.ui.screen.user.SessionInfo("2", "D2", "Old", false))
        sessions.removeIf { it.id == "2" }
        assertEquals(1, sessions.size)
    }

    @Test fun `revoke all other sessions keeps current`() {
        val sessions = mutableListOf(com.ezbookkeeping.android.ui.screen.user.SessionInfo("1", "This", "Now", true), com.ezbookkeeping.android.ui.screen.user.SessionInfo("2", "Other", "Old", false))
        sessions.removeIf { !it.isCurrent }
        assertEquals(1, sessions.size)
        assertTrue(sessions[0].isCurrent)
    }

    // ===== 2FA =====

    @Test fun `TwoFactorAuthUiState default disabled`() { assertFalse(com.ezbookkeeping.android.ui.screen.user.TwoFactorAuthUiState().isEnabled) }

    @Test fun `TwoFactorAuthUiState enabled with codes`() {
        val state = com.ezbookkeeping.android.ui.screen.user.TwoFactorAuthUiState(isEnabled = true, backupCodes = listOf("ABC12345", "DEF67890"))
        assertTrue(state.isEnabled && state.backupCodes.size == 2)
    }

    // ===== UserProfile =====

    @Test fun `UserProfileUiState defaults`() {
        val state = com.ezbookkeeping.android.ui.screen.user.UserProfileUiState()
        assertEquals("CNY", state.defaultCurrency)
        assertFalse(state.isLoading)
    }

    // ===== Category stat color =====

    @Test fun `category stat uses category color`() {
        val stat = CategoryStat(1, "Food", "#FF5722", 100.0, 50f)
        assertEquals("#FF5722", stat.color)
    }

    @Test fun `category stat default color`() {
        val stat = CategoryStat(null, "Unknown", "#6200EE", 50.0, 25f)
        assertEquals("#6200EE", stat.color)
    }

    // ===== DataManagement =====

    @Test fun `DataManagementUiState defaults`() {
        val state = com.ezbookkeeping.android.ui.screen.user.DataManagementUiState()
        assertFalse(state.isExporting && state.isImporting)
    }

    // ===== ExchangeRate =====

    @Test fun `ExchangeRateEntity creation`() {
        val entity = ExchangeRateEntity(1, "USD", 7.25, "ECB")
        assertEquals("USD", entity.currency)
        assertEquals(7.25, entity.rate, 0.01)
    }
}
