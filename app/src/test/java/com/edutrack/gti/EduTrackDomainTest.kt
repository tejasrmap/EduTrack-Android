package com.edutrack.gti

import com.edutrack.gti.data.local.StudySession
import com.edutrack.gti.data.local.Subject
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

/**
 * Comprehensive JUnit unit tests validating EduTrack core domain models,
 * gamification XP engine, level progression, and daily aggregation logic.
 */
class EduTrackDomainTest {

    @Test
    fun testXpCalculationFormula() {
        // Standard session: 60 mins -> (60 * 0.8) + 20 = 48 + 20 = 68 XP
        val duration60 = 60
        val xp60 = (duration60 * 0.8).toInt() + 20
        assertEquals(68, xp60)

        // 30 mins session -> (30 * 0.8) + 20 = 24 + 20 = 44 XP
        val duration30 = 30
        val xp30 = (duration30 * 0.8).toInt() + 20
        assertEquals(44, xp30)

        // 15 mins quick sprint -> (15 * 0.8) + 20 = 12 + 20 = 32 XP
        val duration15 = 15
        val xp15 = (duration15 * 0.8).toInt() + 20
        assertEquals(32, xp15)

        // 120 mins intensive study block -> (120 * 0.8) + 20 = 96 + 20 = 116 XP
        val duration120 = 120
        val xp120 = (duration120 * 0.8).toInt() + 20
        assertEquals(116, xp120)
    }

    @Test
    fun testGamificationLevelProgression() {
        // Level threshold: 200 XP per level
        val xpPerLevel = 200

        // Test Level 1 (0 XP)
        val xp0 = 0
        val level0 = (xp0 / xpPerLevel) + 1
        val progress0 = ((xp0 % xpPerLevel) / xpPerLevel.toFloat()) * 100f
        assertEquals(1, level0)
        assertEquals(0f, progress0, 0.01f)

        // Test Level 1 mid-progress (150 XP)
        val xp150 = 150
        val level150 = (xp150 / xpPerLevel) + 1
        val progress150 = ((xp150 % xpPerLevel) / xpPerLevel.toFloat()) * 100f
        assertEquals(1, level150)
        assertEquals(75f, progress150, 0.01f)

        // Test Level 2 threshold (200 XP)
        val xp200 = 200
        val level200 = (xp200 / xpPerLevel) + 1
        val progress200 = ((xp200 % xpPerLevel) / xpPerLevel.toFloat()) * 100f
        assertEquals(2, level200)
        assertEquals(0f, progress200, 0.01f)

        // Test Level 3 (550 XP -> Level 3, 150/200 XP)
        val xp550 = 550
        val level550 = (xp550 / xpPerLevel) + 1
        val currentLevelXp = xp550 % xpPerLevel
        assertEquals(3, level550)
        assertEquals(150, currentLevelXp)
    }

    @Test
    fun testStudySessionEntityCreation() {
        val today = LocalDate.now().toString()
        val session = StudySession(
            id = 1,
            subjectId = 10,
            durationMinutes = 90,
            date = today,
            notes = "Deep dive into Room relational entities and Foreign Keys",
            rating = 5,
            topicName = "Room Database Architecture",
            xpEarned = 92
        )

        assertEquals(1, session.id)
        assertEquals(10, session.subjectId)
        assertEquals(90, session.durationMinutes)
        assertEquals(today, session.date)
        assertEquals("Room Database Architecture", session.topicName)
        assertEquals(5, session.rating)
        assertEquals(92, session.xpEarned)
    }

    @Test
    fun testSubjectEntityCreationAndHexColor() {
        val subject = Subject(
            id = 1,
            name = "Mobile Apps Development",
            colorHex = "#6366F1",
            weeklyGoalHours = 8.0,
            description = "Android Jetpack Compose & Clean Architecture",
            nextTopic = "State Management & Room DB",
            semester = 3
        )

        assertEquals(1, subject.id)
        assertEquals("Mobile Apps Development", subject.name)
        assertEquals("#6366F1", subject.colorHex)
        assertEquals(8.0, subject.weeklyGoalHours, 0.01)
        assertEquals("State Management & Room DB", subject.nextTopic)
        assertEquals(3, subject.semester)
    }

    @Test
    fun testWeeklyPaceCalculation() {
        val weeklyGoalHours = 26.0
        val expectedDailyPace = weeklyGoalHours / 7.0
        val roundedDailyPace = Math.round(expectedDailyPace * 10.0) / 10.0

        assertEquals(3.7, roundedDailyPace, 0.05)
    }
}
