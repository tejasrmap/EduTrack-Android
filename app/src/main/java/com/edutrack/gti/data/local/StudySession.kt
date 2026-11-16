package com.edutrack.gti.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Room Entity representing a logged study session in EduTrack 2.0.
 */
@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectId: Int,
    val durationMinutes: Int,
    val date: String, // YYYY-MM-DD
    val notes: String,
    val rating: Int = 4, // 1-5 rating
    val topicName: String = "",
    val xpEarned: Int = 40
)

/**
 * Relational POJO using Room @Embedded and @Relation to join StudySession with its parent Subject.
 */
data class StudySessionWithSubject(
    @Embedded
    val session: StudySession,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "id"
    )
    val subject: Subject?
)

/**
 * Projection result class for Room SQL GROUP BY duration aggregation per subject.
 */
data class SubjectDurationSummary(
    val subjectId: Int,
    val totalMinutes: Int
)
