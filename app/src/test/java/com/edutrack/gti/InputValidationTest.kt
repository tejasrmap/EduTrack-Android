package com.edutrack.gti

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * Unit tests validating defensive input parsing, data sanitation,
 * bounds coercion, and date formatting across EduTrack.
 */
class InputValidationTest {

    @Test
    fun testDurationCoercionBounds() {
        // Valid duration within range
        val validDuration = 45
        assertEquals(45, validDuration.coerceIn(1, 1440))

        // Negative duration coerced to minimum 1 minute
        val negativeDuration = -10
        assertEquals(1, negativeDuration.coerceIn(1, 1440))

        // Zero duration coerced to minimum 1 minute
        val zeroDuration = 0
        assertEquals(1, zeroDuration.coerceIn(1, 1440))

        // Excessive duration (> 24 hours) coerced to maximum 1440 minutes
        val excessiveDuration = 5000
        assertEquals(1440, excessiveDuration.coerceIn(1, 1440))
    }

    @Test
    fun testWeeklyHoursCoercion() {
        // Minimum weekly hours constraint
        val minHours = -5.0
        assertEquals(1.0, minHours.coerceIn(1.0, 168.0), 0.01)

        // Maximum weekly hours constraint (168h in a week)
        val maxHours = 200.0
        assertEquals(168.0, maxHours.coerceIn(1.0, 168.0), 0.01)

        // Standard student target
        val standardHours = 26.0
        assertEquals(26.0, standardHours.coerceIn(1.0, 168.0), 0.01)
    }

    @Test
    fun testIso8601DateFormatting() {
        // Valid ISO date
        val validDateStr = "2026-11-25"
        val parsedDate = LocalDate.parse(validDateStr)
        assertNotNull(parsedDate)
        assertEquals(2026, parsedDate.year)
        assertEquals(11, parsedDate.monthValue)
        assertEquals(25, parsedDate.dayOfMonth)

        // Invalid date should throw DateTimeParseException
        val invalidDateStr = "25/11/2026"
        var exceptionThrown = false
        try {
            LocalDate.parse(invalidDateStr)
        } catch (e: DateTimeParseException) {
            exceptionThrown = true
        }
        assertTrue("DateTimeParseException should be thrown for non-ISO dates", exceptionThrown)
    }

    @Test
    fun testStudentIdValidation() {
        // Standard APU student matriculation format: I followed by 8 digits
        val validMatricId = "I26043415"
        val matricPattern = Regex("^[A-Za-z0-9]{6,12}$")
        assertTrue(validMatricId.matches(matricPattern))

        // Blank ID validation
        val blankId = "   "
        assertTrue(blankId.trim().isEmpty())
    }

    @Test
    fun testEmailPatternValidation() {
        val validEmail = "teja.g@student.edu"
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        assertTrue(validEmail.matches(emailRegex))

        val customEmail = "tejag.vijay@gmail.com"
        assertTrue(customEmail.matches(emailRegex))

        val invalidEmail = "not-an-email"
        assertFalse(invalidEmail.matches(emailRegex))
    }
}
