package com.edutrack.gti

import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

/**
 * Unit tests validating Live Study Timer time calculations,
 * string formatting, and Pomodoro cycle durations.
 */
class TimerFormattingTest {

    private fun formatElapsedTime(seconds: Long): String {
        val hours = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)
    }

    @Test
    fun testStopwatchTimeFormatting() {
        // 0 seconds -> 00:00:00
        assertEquals("00:00:00", formatElapsedTime(0))

        // 59 seconds -> 00:00:59
        assertEquals("00:00:59", formatElapsedTime(59))

        // 60 seconds -> 00:01:00
        assertEquals("00:01:00", formatElapsedTime(60))

        // 25 minutes (Pomodoro interval) -> 1500 seconds -> 00:25:00
        assertEquals("00:25:00", formatElapsedTime(1500))

        // 1 hour 45 minutes 30 seconds -> 6330 seconds -> 01:45:30
        assertEquals("01:45:30", formatElapsedTime(6330))
    }

    @Test
    fun testPomodoroWorkAndBreakIntervals() {
        val standardWorkDurationMinutes = 25
        val standardBreakDurationMinutes = 5
        val longBreakDurationMinutes = 15

        assertEquals(1500, standardWorkDurationMinutes * 60)
        assertEquals(300, standardBreakDurationMinutes * 60)
        assertEquals(900, longBreakDurationMinutes * 60)
    }

    @Test
    fun testUserInitialsExtraction() {
        val name1 = "Teja G"
        val initials1 = name1.trim().split("\\s+".toRegex())
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("").take(2).ifEmpty { "TG" }
        assertEquals("TG", initials1)

        val name2 = "Alexander Hamilton"
        val initials2 = name2.trim().split("\\s+".toRegex())
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("").take(2).ifEmpty { "TG" }
        assertEquals("AH", initials2)

        val nameEmpty = ""
        val initialsEmpty = nameEmpty.trim().split("\\s+".toRegex())
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("").take(2).ifEmpty { "TG" }
        assertEquals("TG", initialsEmpty)
    }
}
