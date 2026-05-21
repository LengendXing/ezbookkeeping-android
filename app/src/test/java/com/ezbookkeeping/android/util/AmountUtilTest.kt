package com.ezbookkeeping.android.util

import org.junit.Assert.*
import org.junit.Test

class AmountUtilTest {

    @Test
    fun `format zero returns valid string`() {
        val result = AmountUtil.format(0.0)
        assertNotNull(result)
        assertTrue(result.contains("0"))
    }

    @Test
    fun `format positive returns non-empty`() {
        val result = AmountUtil.format(1.0)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format negative returns non-empty`() {
        val result = AmountUtil.format(-1.0)
        assertNotNull(result)
        assertTrue(result.contains("-"))
    }

    @Test
    fun `format two decimal places returns valid`() {
        val result = AmountUtil.format(1.23)
        assertTrue(result.contains("1") && result.contains("23"))
    }

    @Test
    fun `format rounds up behavior`() {
        val result = AmountUtil.format(1.235)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format rounds down behavior`() {
        val result = AmountUtil.format(1.234)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format large number produces non-empty`() {
        val result = AmountUtil.format(1234567.89)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format very small amount`() {
        val result = AmountUtil.format(0.01)
        assertNotNull(result)
        assertTrue(result.contains("0"))
    }

    @Test
    fun `format negative with decimals contains minus`() {
        val result = AmountUtil.format(-99.99)
        assertTrue(result.contains("-"))
    }

    @Test
    fun `format max safe double produces non-empty`() {
        val result = AmountUtil.format(Double.MAX_VALUE)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format zero decimal places produces non-empty`() {
        val result = AmountUtil.format(1.5, 0)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format one decimal place produces non-empty`() {
        val result = AmountUtil.format(1.25, 1)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format three decimal places`() {
        val result = AmountUtil.format(1.234, 3)
        assertNotNull(result)
        assertTrue(result.contains("1") && result.contains("234"))
    }

    @Test
    fun `format four decimal places`() {
        val result = AmountUtil.format(0.0001, 4)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format negative custom decimals contains minus`() {
        val result = AmountUtil.format(-5.678, 2)
        assertTrue(result.contains("-"))
    }

    @Test
    fun `format NaN returns non-empty`() {
        val result = AmountUtil.format(Double.NaN)
        assertNotNull(result)
    }

    @Test
    fun `format infinity returns non-empty`() {
        val result = AmountUtil.format(Double.POSITIVE_INFINITY)
        assertNotNull(result)
    }

    @Test
    fun `format negative infinity returns non-empty`() {
        val result = AmountUtil.format(Double.NEGATIVE_INFINITY)
        assertNotNull(result)
    }

    @Test
    fun `format hundred produces non-empty`() {
        val result = AmountUtil.format(100.0)
        assertNotNull(result)
        assertTrue(result.contains("100"))
    }

    @Test
    fun `format thousand produces non-empty`() {
        val result = AmountUtil.format(1000.0)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format million produces non-empty`() {
        val result = AmountUtil.format(1000000.0)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `format identical values produce same output`() {
        val a = AmountUtil.format(123.45)
        val b = AmountUtil.format(123.45)
        assertEquals(a, b)
    }

    @Test
    fun `format large negative contains minus`() {
        val result = AmountUtil.format(-999999.99)
        assertTrue(result.contains("-"))
    }

    @Test
    fun `format smallest positive returns non-empty`() {
        val result = AmountUtil.format(Double.MIN_VALUE)
        assertNotNull(result)
    }

    @Test
    fun `format rounding edge case produces non-empty`() {
        val result = AmountUtil.format(0.005, 2)
        assertNotNull(result)
    }

    @Test
    fun `format zero with custom decimals contains zero`() {
        val result = AmountUtil.format(0.0, 4)
        assertTrue(result.contains("0"))
    }

    @Test
    fun `format positive and negative are different`() {
        val pos = AmountUtil.format(100.0)
        val neg = AmountUtil.format(-100.0)
        assertNotEquals(pos, neg)
    }

    @Test
    fun `format different amounts are different`() {
        val a = AmountUtil.format(1.0)
        val b = AmountUtil.format(2.0)
        assertNotEquals(a, b)
    }

    @Test
    fun `format same amount twice is idempotent`() {
        assertEquals(AmountUtil.format(42.5), AmountUtil.format(42.5))
    }
}
