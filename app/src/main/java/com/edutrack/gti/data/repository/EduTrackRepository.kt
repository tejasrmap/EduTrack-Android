package com.edutrack.gti.data.repository

import com.edutrack.gti.data.local.StudySession
import com.edutrack.gti.data.local.StudySessionDao
import com.edutrack.gti.data.local.StudySessionWithSubject
import com.edutrack.gti.data.local.Subject
import com.edutrack.gti.data.local.SubjectDao
import com.edutrack.gti.data.local.SubjectDurationSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository providing a single source of truth for EduTrack app data.
 * Delegates relational queries and SQL aggregations directly to Room DAOs.
 */
class EduTrackRepository(
    private val subjectDao: SubjectDao,
    private val studySessionDao: StudySessionDao
) {
    // Subject Streams & Operations
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()

    fun getSubjectById(id: Int): Flow<Subject?> = subjectDao.getSubjectById(id)

    suspend fun getSubjectByIdOneShot(id: Int): Subject? = subjectDao.getSubjectByIdOneShot(id)

    suspend fun insertSubject(subject: Subject): Long = subjectDao.insertSubject(subject)

    suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)

    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    // StudySession Streams & Relational Queries
    val allSessions: Flow<List<StudySession>> = studySessionDao.getAllSessions()

    /**
     * Relational Room @Transaction stream joining study_sessions with subjects.
     */
    val allSessionsWithSubject: Flow<List<StudySessionWithSubject>> =
        studySessionDao.getAllSessionsWithSubject()

    fun getFilteredSessionsWithSubject(
        subjectId: Int?,
        startDate: String?,
        endDate: String?
    ): Flow<List<StudySessionWithSubject>> =
        studySessionDao.getFilteredSessionsWithSubject(subjectId, startDate, endDate)

    fun getSessionById(id: Int): Flow<StudySession?> = studySessionDao.getSessionById(id)

    suspend fun getSessionByIdOneShot(id: Int): StudySession? = studySessionDao.getSessionByIdOneShot(id)

    fun getSessionsBySubjectId(subjectId: Int): Flow<List<StudySession>> =
        studySessionDao.getSessionsBySubjectId(subjectId)

    // SQL Aggregation Streams
    fun getTotalMinutesForDate(date: String): Flow<Int> =
        studySessionDao.getTotalMinutesForDate(date)

    fun getTotalMinutesInDateRange(startDate: String, endDate: String): Flow<Int> =
        studySessionDao.getTotalMinutesInDateRange(startDate, endDate)

    val totalMinutesPerSubject: Flow<List<SubjectDurationSummary>> =
        studySessionDao.getTotalMinutesPerSubject()

    fun getMinutesPerSubjectInDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<SubjectDurationSummary>> =
        studySessionDao.getMinutesPerSubjectInDateRange(startDate, endDate)

    val totalXpEarned: Flow<Int> = studySessionDao.getTotalXpEarned()

    suspend fun insertSession(session: StudySession): Long = studySessionDao.insertSession(session)

    suspend fun updateSession(session: StudySession) = studySessionDao.updateSession(session)

    suspend fun deleteSession(session: StudySession) = studySessionDao.deleteSession(session)

    suspend fun deleteSessionById(id: Int) = studySessionDao.deleteSessionById(id)
}
