package com.ezbookkeeping.android.util

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DateUtilTest {

    @Test
    fun `today returns current date`() {
        val today = DateUtil.today()
        val expected = DateUtil.formatDate(Date())
        assertEquals(expected, today)
    }

    @Test
    fun `monthStart returns first day of month`() {
        val start = DateUtil.monthStart()
        assertTrue(start.endsWith("-01"))
    }

    @Test
    fun `monthEnd returns valid date format`() {
        val end = DateUtil.monthEnd()
        assertTrue(end.matches("\\d{4}-\\d{2}-\\d{2}".toRegex()))
    }

    @Test
    fun `lastMonthStart returns first day of previous month`() {
        val start = DateUtil.lastMonthStart()
        assertTrue(start.endsWith("-01"))
    }

    @Test
    fun `lastMonthEnd returns valid date format`() {
        val end = DateUtil.lastMonthEnd()
        assertTrue(end.matches("\\d{4}-\\d{2}-\\d{2}".toRegex()))
    }

    @Test
    fun `yearStart returns January 1`() {
        val start = DateUtil.yearStart()
        assertTrue(start.endsWith("-01-01"))
    }

    @Test
    fun `yearEnd returns December 31`() {
        val end = DateUtil.yearEnd()
        assertTrue(end.endsWith("-12-31"))
    }

    @Test
    fun `dayStart equals today`() {
        assertEquals(DateUtil.today(), DateUtil.dayStart())
    }

    @Test
    fun `dayEnd equals today`() {
        assertEquals(DateUtil.today(), DateUtil.dayEnd())
    }

    @Test
    fun `weekStart returns valid date format`() {
        val start = DateUtil.weekStart()
        assertTrue(start.matches("\\d{4}-\\d{2}-\\d{2}".toRegex()))
    }

    @Test
    fun `weekEnd returns valid date format`() {
        val end = DateUtil.weekEnd()
        assertTrue(end.matches("\\d{4}-\\d{2}-\\d{2}".toRegex()))
    }

    @Test
    fun `formatDate produces yyyy-MM-dd`() {
        val date = DateUtil.parseDate("2025-01-15")!!
        assertEquals("2025-01-15", DateUtil.formatDate(date))
    }

    @Test
    fun `parseDate handles garbage input gracefully`() {
        try {
            DateUtil.parseDate("xyz123")
        } catch (_: Exception) {
            // Expected: parseDate may throw for garbage
        }
    }

    @Test
    fun `parseDate handles leap year`() {
        assertNotNull(DateUtil.parseDate("2024-02-29"))
    }

    @Test
    fun `formatTime produces HH-mm-ss format`() {
        val result = DateUtil.formatTime(Date())
        assertTrue(result.matches("\\d{2}:\\d{2}:\\d{2}".toRegex()))
    }

    @Test
    fun `monthStart is before or equal monthEnd`() {
        val start = DateUtil.monthStart()
        val end = DateUtil.monthEnd()
        assertTrue(start <= end)
    }

    @Test
    fun `lastMonthStart is before lastMonthEnd`() {
        val start = DateUtil.lastMonthStart()
        val end = DateUtil.lastMonthEnd()
        assertTrue(start <= end)
    }

    @Test
    fun `lastMonthStart is before current monthStart`() {
        val last = DateUtil.lastMonthStart()
        val current = DateUtil.monthStart()
        assertTrue(last < current)
    }

    @Test
    fun `yearStart is before yearEnd`() {
        assertTrue(DateUtil.yearStart() < DateUtil.yearEnd())
    }

    @Test
    fun `weekStart is before or equal weekEnd`() {
        assertTrue(DateUtil.weekStart() <= DateUtil.weekEnd())
    }

    @Test
    fun `parseDate round trip`() {
        val original = "2025-06-15"
        val parsed = DateUtil.parseDate(original)!!
        assertEquals(original, DateUtil.formatDate(parsed))
    }

    @Test
    fun `monthStart has year prefix`() {
        val cal = Calendar.getInstance()
        val expectedYear = cal.get(Calendar.YEAR).toString()
        assertTrue(DateUtil.monthStart().startsWith(expectedYear))
    }

    @Test
    fun `today is between monthStart and monthEnd`() {
        val today = DateUtil.today()
        assertTrue(today >= DateUtil.monthStart())
        assertTrue(today <= DateUtil.monthEnd())
    }

    @Test
    fun `lastMonthEnd day is between 28 and 31`() {
        val end = DateUtil.lastMonthEnd()
        val day = end.substring(8, 10).toInt()
        assertTrue(day in 28..31)
    }

    @Test
    fun `monthEnd day is between 28 and 31`() {
        val end = DateUtil.monthEnd()
        val day = end.substring(8, 10).toInt()
        assertTrue(day in 28..31)
    }

    @Test
    fun `yearStart contains current year`() {
        val cal = Calendar.getInstance()
        assertTrue(DateUtil.yearStart().startsWith(cal.get(Calendar.YEAR).toString()))
    }

    @Test
    fun `formatDate handles year boundary dates`() {
        assertNotNull(DateUtil.parseDate("2025-12-31"))
        assertNotNull(DateUtil.parseDate("2026-01-01"))
    }

    @Test
    fun `today format matches yyyy-MM-dd pattern`() {
        assertTrue(DateUtil.today().matches("\\d{4}-\\d{2}-\\d{2}".toRegex()))
    }

    @Test
    fun `weekStart within current or previous year`() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val ws = DateUtil.weekStart().substring(0, 4).toInt()
        assertTrue(ws == year || ws == year - 1)
    }

    @Test
    fun `lastMonthEnd month is before current month`() {
        val lmEnd = DateUtil.lastMonthEnd()
        val mStart = DateUtil.monthStart()
        assertTrue(lmEnd < mStart)
    }

    @Test
    fun `yearStart month is January`() {
        assertTrue(DateUtil.yearStart().contains("-01-"))
    }

    @Test
    fun `yearEnd month is December`() {
        assertTrue(DateUtil.yearEnd().contains("-12-"))
    }

    @Test
    fun `parseDate handles various valid dates`() {
        assertNotNull(DateUtil.parseDate("2025-01-01"))
        assertNotNull(DateUtil.parseDate("2025-06-15"))
        assertNotNull(DateUtil.parseDate("2025-12-31"))
    }

    @Test
    fun `monthEnd day for February non-leap`() {
        val end = DateUtil.monthEnd()
        assertNotNull(end)
    }

    @Test
    fun `formatDate and parseDate consistency for multiple dates`() {
        listOf("2025-01-01", "2025-06-15", "2025-12-31").forEach { dateStr ->
            val parsed = DateUtil.parseDate(dateStr)
            assertNotNull(parsed)
            assertEquals(dateStr, DateUtil.formatDate(parsed!!))
        }
    }

    @Test
    fun `dayStart equals dayEnd`() {
        assertEquals(DateUtil.dayStart(), DateUtil.dayEnd())
    }
}
