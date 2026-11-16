package com.edutrack.gti.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for StudySession entity, including relational joins and SQL aggregations.
 */
@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY date DESC, id DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Transaction
    @Query("SELECT * FROM study_sessions ORDER BY date DESC, id DESC")
    fun getAllSessionsWithSubject(): Flow<List<StudySessionWithSubject>>

    @Transaction
    @Query(
        """
        SELECT * FROM study_sessions
        WHERE (:subjectId IS NULL OR subjectId = :subjectId)
          AND (:startDate IS NULL OR date >= :startDate)
          AND (:endDate IS NULL OR date <= :endDate)
        ORDER BY date DESC, id DESC
        """
    )
    fun getFilteredSessionsWithSubject(
        subjectId: Int?,
        startDate: String?,
        endDate: String?
    ): Flow<List<StudySessionWithSubject>>

    @Query("SELECT * FROM study_sessions WHERE id = :id")
    fun getSessionById(id: Int): Flow<StudySession?>

    @Query("SELECT * FROM study_sessions WHERE id = :id")
    suspend fun getSessionByIdOneShot(id: Int): StudySession?

    @Query("SELECT * FROM study_sessions WHERE subjectId = :subjectId ORDER BY date DESC")
    fun getSessionsBySubjectId(subjectId: Int): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE date = :date")
    fun getSessionsForDate(date: String): Flow<List<StudySession>>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_sessions WHERE date = :date")
    fun getTotalMinutesForDate(date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalMinutesInDateRange(startDate: String, endDate: String): Flow<Int>

    @Query("SELECT subjectId, COALESCE(SUM(durationMinutes), 0) AS totalMinutes FROM study_sessions GROUP BY subjectId")
    fun getTotalMinutesPerSubject(): Flow<List<SubjectDurationSummary>>

    @Query(
        """
        SELECT subjectId, COALESCE(SUM(durationMinutes), 0) AS totalMinutes
        FROM study_sessions
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY subjectId
        """
    )
    fun getMinutesPerSubjectInDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<SubjectDurationSummary>>

    @Query("SELECT COALESCE(SUM(xpEarned), 0) FROM study_sessions")
    fun getTotalXpEarned(): Flow<Int>

    @Query("UPDATE study_sessions SET subjectId = :newSubjectId WHERE subjectId = :oldSubjectId")
    suspend fun reassignSessionsSubject(oldSubjectId: Int, newSubjectId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Update
    suspend fun updateSession(session: StudySession)

    @Delete
    suspend fun deleteSession(session: StudySession)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)
}
