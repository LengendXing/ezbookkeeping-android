package com.ezbookkeeping.android.entity

import com.ezbookkeeping.android.data.db.entity.*
import org.junit.Assert.*
import org.junit.Test

class EnumsTest {

    // --- AccountType ---
    @Test
    fun `AccountType has ASSET`() { assertNotNull(AccountType.valueOf("ASSET")) }

    @Test
    fun `AccountType has LIABILITY`() { assertNotNull(AccountType.valueOf("LIABILITY")) }

    @Test
    fun `AccountType has exactly 2 values`() { assertEquals(2, AccountType.values().size) }

    @Test(expected = IllegalArgumentException::class)
    fun `AccountType invalid name throws`() { AccountType.valueOf("INVALID") }

    // --- TransactionType ---
    @Test
    fun `TransactionType has EXPENSE`() { assertNotNull(TransactionType.valueOf("EXPENSE")) }

    @Test
    fun `TransactionType has INCOME`() { assertNotNull(TransactionType.valueOf("INCOME")) }

    @Test
    fun `TransactionType has TRANSFER`() { assertNotNull(TransactionType.valueOf("TRANSFER")) }

    @Test
    fun `TransactionType has exactly 3 values`() { assertEquals(3, TransactionType.values().size) }

    @Test(expected = IllegalArgumentException::class)
    fun `TransactionType invalid name throws`() { TransactionType.valueOf("INVALID") }

    // --- CategoryType ---
    @Test
    fun `CategoryType has EXPENSE`() { assertNotNull(CategoryType.valueOf("EXPENSE")) }

    @Test
    fun `CategoryType has INCOME`() { assertNotNull(CategoryType.valueOf("INCOME")) }

    @Test
    fun `CategoryType has TRANSFER`() { assertNotNull(CategoryType.valueOf("TRANSFER")) }

    @Test
    fun `CategoryType has exactly 3 values`() { assertEquals(3, CategoryType.values().size) }

    // --- TokenRecordType ---
    @Test
    fun `TokenRecordType has ACCESS`() { assertNotNull(TokenRecordType.valueOf("ACCESS")) }

    @Test
    fun `TokenRecordType has REFRESH`() { assertNotNull(TokenRecordType.valueOf("REFRESH")) }

    @Test
    fun `TokenRecordType has exactly 2 values`() { assertEquals(2, TokenRecordType.values().size) }

    // --- ImportStatus ---
    @Test
    fun `ImportStatus has PENDING`() { assertNotNull(ImportStatus.valueOf("PENDING")) }

    @Test
    fun `ImportStatus has PROCESSING`() { assertNotNull(ImportStatus.valueOf("PROCESSING")) }

    @Test
    fun `ImportStatus has COMPLETED`() { assertNotNull(ImportStatus.valueOf("COMPLETED")) }

    @Test
    fun `ImportStatus has FAILED`() { assertNotNull(ImportStatus.valueOf("FAILED")) }

    @Test
    fun `ImportStatus has exactly 4 values`() { assertEquals(4, ImportStatus.values().size) }

    // --- LockType ---
    @Test
    fun `LockType has NONE`() { assertNotNull(LockType.valueOf("NONE")) }

    @Test
    fun `LockType has PIN`() { assertNotNull(LockType.valueOf("PIN")) }

    @Test
    fun `LockType has PASSWORD`() { assertNotNull(LockType.valueOf("PASSWORD")) }

    @Test
    fun `LockType has BIOMETRIC`() { assertNotNull(LockType.valueOf("BIOMETRIC")) }

    @Test
    fun `LockType has exactly 4 values`() { assertEquals(4, LockType.values().size) }

    // --- UserRole ---
    @Test
    fun `UserRole has ADMIN`() { assertNotNull(UserRole.valueOf("ADMIN")) }

    @Test
    fun `UserRole has USER`() { assertNotNull(UserRole.valueOf("USER")) }

    @Test
    fun `UserRole has exactly 2 values`() { assertEquals(2, UserRole.values().size) }

    // --- Cross-type consistency ---
    @Test
    fun `TransactionType and CategoryType names match`() {
        TransactionType.values().forEach { tt ->
            assertNotNull("CategoryType missing ${tt.name}", CategoryType.valueOf(tt.name))
        }
    }

    @Test
    fun `AccountType names are distinct`() {
        val names = AccountType.values().map { it.name }.toSet()
        assertEquals(AccountType.values().size, names.size)
    }

    @Test
    fun `TransactionType names are distinct`() {
        val names = TransactionType.values().map { it.name }.toSet()
        assertEquals(TransactionType.values().size, names.size)
    }

    @Test
    fun `LockType names are distinct`() {
        val names = LockType.values().map { it.name }.toSet()
        assertEquals(LockType.values().size, names.size)
    }

    @Test
    fun `ImportStatus order is PENDING PROCESSING COMPLETED FAILED`() {
        val expected = listOf("PENDING", "PROCESSING", "COMPLETED", "FAILED")
        assertEquals(expected, ImportStatus.values().map { it.name })
    }

    @Test
    fun `LockType NONE is first`() { assertEquals("NONE", LockType.values().first().name) }

    @Test
    fun `AccountType ASSET is first`() { assertEquals("ASSET", AccountType.values().first().name) }

    @Test
    fun `TransactionType EXPENSE is first`() { assertEquals("EXPENSE", TransactionType.values().first().name) }

    @Test
    fun `CategoryType EXPENSE is first`() { assertEquals("EXPENSE", CategoryType.values().first().name) }

    @Test
    fun `UserRole ADMIN is first`() { assertEquals("ADMIN", UserRole.values().first().name) }

    @Test
    fun `TokenRecordType ACCESS is first`() { assertEquals("ACCESS", TokenRecordType.values().first().name) }

    @Test
    fun `all enum values have name property`() {
        AccountType.values().forEach { assertNotNull(it.name) }
        TransactionType.values().forEach { assertNotNull(it.name) }
        CategoryType.values().forEach { assertNotNull(it.name) }
        LockType.values().forEach { assertNotNull(it.name) }
    }

    @Test
    fun `enum valueOf round trip for all types`() {
        AccountType.values().forEach { assertEquals(it, AccountType.valueOf(it.name)) }
        TransactionType.values().forEach { assertEquals(it, TransactionType.valueOf(it.name)) }
        CategoryType.values().forEach { assertEquals(it, CategoryType.valueOf(it.name)) }
        LockType.values().forEach { assertEquals(it, LockType.valueOf(it.name)) }
        ImportStatus.values().forEach { assertEquals(it, ImportStatus.valueOf(it.name)) }
        UserRole.values().forEach { assertEquals(it, UserRole.valueOf(it.name)) }
        TokenRecordType.values().forEach { assertEquals(it, TokenRecordType.valueOf(it.name)) }
    }
}
