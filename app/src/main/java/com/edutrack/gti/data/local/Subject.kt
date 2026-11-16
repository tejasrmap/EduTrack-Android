package com.edutrack.gti.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing an academic subject or course in EduTrack 2.0.
 */
@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val colorHex: String,
    val weeklyGoalHours: Double,
    val description: String = "",
    val nextTopic: String = "Introduction",
    val topicsJson: String = "[\"Introduction\", \"Core Concepts\", \"Advanced Practice\", \"Review\"]",
    val semester: Int = 3
)
