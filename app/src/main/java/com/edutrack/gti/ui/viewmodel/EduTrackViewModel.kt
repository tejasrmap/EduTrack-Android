package com.edutrack.gti.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.edutrack.gti.data.local.AppDatabase
import com.edutrack.gti.data.local.StudySession
import com.edutrack.gti.data.local.StudySessionWithSubject
import com.edutrack.gti.data.local.Subject
import com.edutrack.gti.data.repository.EduTrackRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

enum class DateRangeFilter(val label: String) {
    ALL_TIME("All Dates"),
    TODAY("Today"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days")
}

data class DailyStudyBar(
    val date: String,
    val dayLabel: String,
    val minutes: Int,
    val hours: Double,
    val isToday: Boolean
)

data class DashboardStats(
    val totalMinutesToday: Int = 0,
    val totalHoursThisWeek: Double = 0.0,
    val totalWeeklyGoalHours: Double = 0.0,
    val weeklyGoalProgressPercent: Int = 0,
    val activeStreakDays: Int = 0,
    val totalSessionsCount: Int = 0,
    val level: Int = 1,
    val currentLevelXp: Int = 0,
    val maxLevelXp: Int = 200,
    val totalXp: Int = 0,
    val bestStudyDayName: String = "No data yet",
    val bestStudyDayDurationText: String = "0m logged",
    val mostStudiedSubjectName: String = "No data yet",
    val mostStudiedSubjectHoursText: String = "0.0h total time"
)

data class SessionFormState(
    val id: Int = 0,
    val selectedSubjectId: Int? = null,
    val topicText: String = "",
    val durationText: String = "60",
    val dateText: String = LocalDate.now().toString(),
    val notesText: String = "",
    val rating: Int = 4,
    val xpEarned: Int = 68,
    val subjectError: String? = null,
    val durationError: String? = null,
    val dateError: String? = null
)

data class SubjectFormState(
    val id: Int = 0,
    val nameText: String = "",
    val colorHex: String = "#6366F1",
    val goalHoursText: String = "5.0",
    val descriptionText: String = "",
    val nextTopic: String = "Introduction",
    val topicsJson: String = "[\"Introduction\", \"Core Concepts\", \"Advanced Practice\", \"Review\"]",
    val semester: Int = 3,
    val nameError: String? = null,
    val goalHoursError: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class EduTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EduTrackRepository
    private val prefs = application.getSharedPreferences("edutrack_user_prefs", Context.MODE_PRIVATE)

    // Authentication & User Session State
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow(prefs.getString("user_email", "teja.g@student.edu") ?: "teja.g@student.edu")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _currentSemester = MutableStateFlow(prefs.getInt("current_semester", 3))
    val currentSemester: StateFlow<Int> = _currentSemester.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean("is_onboarding_completed", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "Teja G") ?: "Teja G")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _studentId = MutableStateFlow(prefs.getString("student_id", "I26043415") ?: "I26043415")
    val studentId: StateFlow<String> = _studentId.asStateFlow()

    private val _targetWeeklyHours = MutableStateFlow(prefs.getFloat("target_weekly_hours", 26f).toDouble())
    val targetWeeklyHours: StateFlow<Double> = _targetWeeklyHours.asStateFlow()

    private val _primaryMajor = MutableStateFlow(prefs.getString("primary_major", "Mobile Apps & Software Eng") ?: "Mobile Apps & Software Eng")
    val primaryMajor: StateFlow<String> = _primaryMajor.asStateFlow()

    fun completeOnboarding(
        name: String,
        id: String,
        weeklyHours: Double,
        major: String
    ) {
        val trimmedName = name.trim().ifEmpty { "Teja G" }
        val trimmedId = id.trim().ifEmpty { "I26043415" }
        val trimmedMajor = major.trim().ifEmpty { "Mobile Apps & Software Eng" }

        prefs.edit()
            .putString("user_name", trimmedName)
            .putString("student_id", trimmedId)
            .putFloat("target_weekly_hours", weeklyHours.toFloat())
            .putString("primary_major", trimmedMajor)
            .putBoolean("is_onboarding_completed", true)
            .apply()

        _userName.value = trimmedName
        _studentId.value = trimmedId
        _targetWeeklyHours.value = weeklyHours
        _primaryMajor.value = trimmedMajor
        _isOnboardingCompleted.value = true

        viewModelScope.launch {
            _snackbarEvent.emit("Welcome to EduTrack, $trimmedName!")
        }
    }

    fun login(email: String, pass: String): Boolean {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        val savedEmail = prefs.getString("user_email", "teja.g@student.edu") ?: "teja.g@student.edu"
        val savedPass = prefs.getString("user_password", "Student123!") ?: "Student123!"

        // Accept registered credentials, demo student account, or valid student format
        val isDefaultStudent = (trimmedEmail.equals("teja.g@student.edu", ignoreCase = true) || 
                                trimmedEmail.equals("student@edutrack.edu", ignoreCase = true)) && 
                                (trimmedPass == "Student123!" || trimmedPass.isNotEmpty())
        val isSavedAccount = trimmedEmail.equals(savedEmail, ignoreCase = true) && 
                             (trimmedPass == savedPass || savedPass.isEmpty() || trimmedPass == "Student123!")

        if (isDefaultStudent || isSavedAccount || (trimmedEmail.contains("@") && trimmedPass.length >= 6)) {
            prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("user_email", trimmedEmail)
                .apply()
            _userEmail.value = trimmedEmail
            _isLoggedIn.value = true
            viewModelScope.launch {
                _snackbarEvent.emit("Welcome back, ${_userName.value}!")
            }
            return true
        }
        return false
    }

    fun register(
        name: String,
        email: String,
        pass: String,
        studentId: String,
        major: String,
        weeklyHours: Double,
        semester: Int
    ) {
        val trimmedName = name.trim().ifEmpty { "Teja G" }
        val trimmedEmail = email.trim().ifEmpty { "teja.g@student.edu" }
        val trimmedPass = pass.trim().ifEmpty { "Student123!" }
        val trimmedId = studentId.trim().ifEmpty { "I26043415" }
        val trimmedMajor = major.trim().ifEmpty { "Mobile Apps & Software Eng" }
        val validHours = if (weeklyHours > 0) weeklyHours else 26.0

        prefs.edit()
            .putString("user_name", trimmedName)
            .putString("user_email", trimmedEmail)
            .putString("user_password", trimmedPass)
            .putString("student_id", trimmedId)
            .putString("primary_major", trimmedMajor)
            .putFloat("target_weekly_hours", validHours.toFloat())
            .putInt("current_semester", semester)
            .putBoolean("is_logged_in", true)
            .putBoolean("is_onboarding_completed", true)
            .apply()

        _userName.value = trimmedName
        _userEmail.value = trimmedEmail
        _studentId.value = trimmedId
        _primaryMajor.value = trimmedMajor
        _targetWeeklyHours.value = validHours
        _currentSemester.value = semester
        _isLoggedIn.value = true
        _isOnboardingCompleted.value = true

        viewModelScope.launch {
            _snackbarEvent.emit("Account created! Welcome to EduTrack, $trimmedName!")
        }
    }

    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .apply()
        _isLoggedIn.value = false
        viewModelScope.launch {
            _snackbarEvent.emit("Logged out successfully.")
        }
    }

    fun updateUserName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotEmpty()) {
            prefs.edit().putString("user_name", trimmed).apply()
            _userName.value = trimmed
        }
    }

    fun updateUserProfile(
        name: String,
        email: String = _userEmail.value,
        id: String,
        major: String,
        weeklyGoalHours: Double,
        semester: Int = _currentSemester.value
    ) {
        val trimmedName = name.trim().ifEmpty { "Teja G" }
        val trimmedEmail = email.trim().ifEmpty { "teja.g@student.edu" }
        val trimmedId = id.trim().ifEmpty { "I26043415" }
        val trimmedMajor = major.trim().ifEmpty { "Mobile Apps & Software Eng" }
        val validHours = if (weeklyGoalHours > 0) weeklyGoalHours else 26.0

        prefs.edit()
            .putString("user_name", trimmedName)
            .putString("user_email", trimmedEmail)
            .putString("student_id", trimmedId)
            .putString("primary_major", trimmedMajor)
            .putFloat("target_weekly_hours", validHours.toFloat())
            .putInt("current_semester", semester)
            .apply()

        _userName.value = trimmedName
        _userEmail.value = trimmedEmail
        _studentId.value = trimmedId
        _primaryMajor.value = trimmedMajor
        _targetWeeklyHours.value = validHours
        _currentSemester.value = semester

        viewModelScope.launch {
            _snackbarEvent.emit("Profile updated successfully!")
        }
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = EduTrackRepository(database.subjectDao(), database.studySessionDao())
        viewModelScope.launch {
            AppDatabase.seedInitialDataIfEmpty(database)
        }
    }

    // Reactive Data Streams from Room
    val subjects: StateFlow<List<Subject>> = repository.allSubjects.asStateFlow(emptyList())

    val sessionsWithSubject: StateFlow<List<StudySessionWithSubject>> =
        repository.allSessionsWithSubject.asStateFlow(emptyList())

    // Room SQL GROUP BY Streams for Weekly and All-Time Subject Totals
    val weeklyMinutesBySubject: StateFlow<Map<Int, Int>> =
        repository.getMinutesPerSubjectInDateRange(
            startDate = LocalDate.now().minusDays(6).toString(),
            endDate = LocalDate.now().toString()
        ).map { list -> list.associate { it.subjectId to it.totalMinutes } }
            .asStateFlow(emptyMap())

    val totalMinutesBySubject: StateFlow<Map<Int, Int>> =
        repository.totalMinutesPerSubject
            .map { list -> list.associate { it.subjectId to it.totalMinutes } }
            .asStateFlow(emptyMap())

    val weeklyDailyStudyBars: StateFlow<List<DailyStudyBar>> =
        sessionsWithSubject.map { sessions ->
            val today = LocalDate.now()
            (6 downTo 0).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())
                val dateStr = date.toString()
                val minutes = sessions.filter { it.session.date == dateStr }.sumOf { it.session.durationMinutes }
                val hours = Math.round((minutes / 60.0) * 10) / 10.0
                val dayLabel = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                DailyStudyBar(
                    date = dateStr,
                    dayLabel = dayLabel,
                    minutes = minutes,
                    hours = hours,
                    isToday = daysAgo == 0
                )
            }
        }.asStateFlow(emptyList())

    // Filtering & Searching State (Subject + Date Range + Text Search)
    private val _selectedSubjectFilterId = MutableStateFlow<Int?>(null)
    val selectedSubjectFilterId: StateFlow<Int?> = _selectedSubjectFilterId.asStateFlow()

    private val _selectedDateRangeFilter = MutableStateFlow(DateRangeFilter.ALL_TIME)
    val selectedDateRangeFilter: StateFlow<DateRangeFilter> = _selectedDateRangeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered Sessions Stream backed by Room SQL query + live search refinement
    val filteredSessions: StateFlow<List<StudySessionWithSubject>> =
        combine(_selectedSubjectFilterId, _selectedDateRangeFilter) { subjectId, dateFilter ->
            val today = LocalDate.now()
            val (startDate, endDate) = when (dateFilter) {
                DateRangeFilter.ALL_TIME -> null to null
                DateRangeFilter.TODAY -> today.toString() to today.toString()
                DateRangeFilter.LAST_7_DAYS -> today.minusDays(6).toString() to today.toString()
                DateRangeFilter.LAST_30_DAYS -> today.minusDays(29).toString() to today.toString()
            }
            Triple(subjectId, startDate, endDate)
        }.flatMapLatest { (subjectId, startDate, endDate) ->
            repository.getFilteredSessionsWithSubject(subjectId, startDate, endDate)
        }.combine(_searchQuery) { sqlFilteredSessions, query ->
            if (query.isBlank()) {
                sqlFilteredSessions
            } else {
                sqlFilteredSessions.filter { item ->
                    (item.subject?.name?.contains(query, ignoreCase = true) == true) ||
                            item.session.notes.contains(query, ignoreCase = true) ||
                            item.session.topicName.contains(query, ignoreCase = true)
                }
            }
        }.asStateFlow(emptyList())

    // Dashboard & Analytics Statistics Calculation backed by Room SQL streams
    private val todayMinutesFlow: Flow<Int> =
        repository.getTotalMinutesForDate(LocalDate.now().toString())

    private val weeklyMinutesFlow: Flow<Int> =
        repository.getTotalMinutesInDateRange(
            startDate = LocalDate.now().minusDays(6).toString(),
            endDate = LocalDate.now().toString()
        )

    val dashboardStats: StateFlow<DashboardStats> =
        combine(
            sessionsWithSubject,
            subjects,
            todayMinutesFlow,
            weeklyMinutesFlow,
            repository.totalXpEarned
        ) { sessions, subjectList, todayMinutes, weeklyMinutes, earnedXp ->
            val now = LocalDate.now()
            val weeklyHours = weeklyMinutes / 60.0
            val totalWeeklyGoal = subjectList.sumOf { it.weeklyGoalHours }

            val progressPercent = if (totalWeeklyGoal > 0) {
                ((weeklyHours / totalWeeklyGoal) * 100).coerceIn(0.0, 100.0).toInt()
            } else 0

            // Active Streak Calculation
            val sortedUniqueDates = sessions
                .mapNotNull {
                    try {
                        LocalDate.parse(it.session.date)
                    } catch (e: Exception) {
                        null
                    }
                }
                .distinct()
                .sortedDescending()

            var streak = 0
            if (sortedUniqueDates.isNotEmpty()) {
                var checkDate = if (sortedUniqueDates.contains(now)) now else now.minusDays(1)
                while (sortedUniqueDates.contains(checkDate)) {
                    streak++
                    checkDate = checkDate.minusDays(1)
                }
            }

            // Gamified XP & Level Calculation (200 XP per level)
            val xpPerLevel = 200
            val level = 1 + (earnedXp / xpPerLevel)
            val currentLevelXp = earnedXp % xpPerLevel

            // Best Study Day of Week Calculation
            val minutesByDayOfWeek = sessions.mapNotNull { item ->
                try {
                    LocalDate.parse(item.session.date).dayOfWeek to item.session.durationMinutes
                } catch (e: Exception) {
                    null
                }
            }.groupBy({ it.first }, { it.second })
                .mapValues { (_, mins) -> mins.sum() }

            val bestDayEntry = minutesByDayOfWeek.maxByOrNull { it.value }
            val bestDayName = bestDayEntry?.key?.getDisplayName(TextStyle.FULL, Locale.ENGLISH) ?: "No sessions yet"
            val bestDayMins = bestDayEntry?.value ?: 0
            val bestDayDurationText = if (bestDayMins >= 60) {
                "${bestDayMins / 60}h ${bestDayMins % 60}m logged"
            } else {
                "${bestDayMins}m logged"
            }

            // Most Studied Subject Calculation
            val minutesBySubjectId = sessions
                .groupBy({ it.session.subjectId }, { it.session.durationMinutes })
                .mapValues { (_, mins) -> mins.sum() }
            val topSubjectEntry = minutesBySubjectId.maxByOrNull { it.value }
            val topSubjectName = subjectList.find { it.id == topSubjectEntry?.key }?.name ?: "No sessions yet"
            val topSubjectHours = Math.round(((topSubjectEntry?.value ?: 0) / 60.0) * 10) / 10.0

            DashboardStats(
                totalMinutesToday = todayMinutes,
                totalHoursThisWeek = Math.round(weeklyHours * 10) / 10.0,
                totalWeeklyGoalHours = totalWeeklyGoal,
                weeklyGoalProgressPercent = progressPercent,
                activeStreakDays = streak,
                totalSessionsCount = sessions.size,
                level = level,
                currentLevelXp = currentLevelXp,
                maxLevelXp = xpPerLevel,
                totalXp = earnedXp,
                bestStudyDayName = bestDayName,
                bestStudyDayDurationText = bestDayDurationText,
                mostStudiedSubjectName = topSubjectName,
                mostStudiedSubjectHoursText = "${topSubjectHours}h total time"
            )
        }.asStateFlow(DashboardStats())

    // Form States
    private val _sessionFormState = MutableStateFlow(SessionFormState())
    val sessionFormState: StateFlow<SessionFormState> = _sessionFormState.asStateFlow()

    private val _subjectFormState = MutableStateFlow(SubjectFormState())
    val subjectFormState: StateFlow<SubjectFormState> = _subjectFormState.asStateFlow()

    // Feedback Channel
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    // Filter Controls
    fun setSubjectFilter(subjectId: Int?) {
        _selectedSubjectFilterId.value = subjectId
    }

    fun setDateRangeFilter(filter: DateRangeFilter) {
        _selectedDateRangeFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Session Form Handlers
    fun prepareNewSessionForm(defaultSubjectId: Int? = null) {
        val firstSub = if (defaultSubjectId != null) {
            subjects.value.find { it.id == defaultSubjectId }
        } else {
            subjects.value.firstOrNull()
        }
        _sessionFormState.value = SessionFormState(
            id = 0,
            selectedSubjectId = firstSub?.id,
            topicText = firstSub?.nextTopic ?: "",
            dateText = LocalDate.now().toString(),
            durationText = "60"
        )
    }

    fun prepareEditSessionForm(sessionWithSubject: StudySessionWithSubject) {
        val s = sessionWithSubject.session
        _sessionFormState.value = SessionFormState(
            id = s.id,
            selectedSubjectId = s.subjectId,
            topicText = s.topicName,
            durationText = s.durationMinutes.toString(),
            dateText = s.date,
            notesText = s.notes,
            rating = s.rating,
            xpEarned = s.xpEarned
        )
    }

    fun updateSessionSubject(subjectId: Int) {
        val selectedSub = subjects.value.find { it.id == subjectId }
        val currentTopic = _sessionFormState.value.topicText
        _sessionFormState.value = _sessionFormState.value.copy(
            selectedSubjectId = subjectId,
            topicText = if (_sessionFormState.value.id == 0 && currentTopic.isBlank()) {
                selectedSub?.nextTopic ?: ""
            } else currentTopic,
            subjectError = null
        )
    }

    fun updateSessionTopic(topicText: String) {
        _sessionFormState.value = _sessionFormState.value.copy(topicText = topicText)
    }

    fun updateSessionDuration(durationText: String) {
        _sessionFormState.value = _sessionFormState.value.copy(
            durationText = durationText,
            durationError = null
        )
    }

    fun updateSessionDate(dateText: String) {
        _sessionFormState.value = _sessionFormState.value.copy(
            dateText = dateText,
            dateError = null
        )
    }

    fun updateSessionNotes(notesText: String) {
        _sessionFormState.value = _sessionFormState.value.copy(notesText = notesText)
    }

    fun updateSessionRating(rating: Int) {
        _sessionFormState.value = _sessionFormState.value.copy(rating = rating)
    }

    fun validateAndSaveSession(onSuccess: () -> Unit) {
        val state = _sessionFormState.value

        var isValid = true
        var subError: String? = null
        var durError: String? = null
        var dtError: String? = null

        if (state.selectedSubjectId == null || state.selectedSubjectId <= 0) {
            subError = "Please select a subject"
            isValid = false
        }

        val durationVal = state.durationText.toIntOrNull()
        if (durationVal == null || durationVal <= 0) {
            durError = "Enter a valid duration (greater than 0 minutes)"
            isValid = false
        } else if (durationVal > 1440) {
            durError = "Duration cannot exceed 1440 minutes (24 hours)"
            isValid = false
        }

        if (state.dateText.isBlank()) {
            dtError = "Date cannot be empty"
            isValid = false
        } else {
            try {
                LocalDate.parse(state.dateText.trim())
            } catch (e: Exception) {
                dtError = "Invalid date format. Use YYYY-MM-DD"
                isValid = false
            }
        }

        _sessionFormState.value = state.copy(
            subjectError = subError,
            durationError = durError,
            dateError = dtError
        )

        if (!isValid) return

        viewModelScope.launch {
            val computedXp = (durationVal!! * 0.8).toInt() + 20
            val selectedSubject = subjects.value.find { it.id == state.selectedSubjectId }
            val resolvedTopic = state.topicText.trim().ifBlank { selectedSubject?.nextTopic ?: "Core Study" }

            val session = StudySession(
                id = state.id,
                subjectId = state.selectedSubjectId!!,
                durationMinutes = durationVal,
                date = state.dateText.trim(),
                notes = state.notesText.trim(),
                rating = state.rating,
                topicName = resolvedTopic,
                xpEarned = computedXp
            )

            if (state.id == 0) {
                repository.insertSession(session)
                _snackbarEvent.emit("Study session logged (+$computedXp XP)!")
            } else {
                repository.updateSession(session)
                _snackbarEvent.emit("Study session updated successfully!")
            }
            onSuccess()
        }
    }

    /**
     * Dedicated handler for saving completed Live Timer sessions as brand-new records
     * so an earlier Edit Session form state is never overwritten.
     */
    fun logCompletedTimerSession(
        subjectId: Int,
        durationMinutes: Int,
        topicName: String,
        notes: String,
        onSuccess: () -> Unit
    ) {
        val validSubjectId = if (subjectId > 0) subjectId else subjects.value.firstOrNull()?.id ?: return
        val safeMinutes = durationMinutes.coerceIn(1, 1440)
        val xpReward = (safeMinutes * 0.8).toInt() + 20
        val resolvedTopic = topicName.trim().ifBlank { "Focused Timer Session" }

        viewModelScope.launch {
            repository.insertSession(
                StudySession(
                    id = 0,
                    subjectId = validSubjectId,
                    durationMinutes = safeMinutes,
                    date = LocalDate.now().toString(),
                    notes = notes.trim().ifBlank { "Live timer session completed for $resolvedTopic" },
                    rating = 5,
                    topicName = resolvedTopic,
                    xpEarned = xpReward
                )
            )
            _snackbarEvent.emit("Timer session saved (+$xpReward XP)!")
            onSuccess()
        }
    }

    fun deleteSession(session: StudySession, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteSession(session)
            _snackbarEvent.emit("Study session deleted.")
            onSuccess()
        }
    }

    // Subject Form Handlers & Validation (preserving all columns on @Update)
    fun prepareNewSubjectForm() {
        _subjectFormState.value = SubjectFormState()
    }

    fun prepareEditSubjectForm(subject: Subject) {
        _subjectFormState.value = SubjectFormState(
            id = subject.id,
            nameText = subject.name,
            colorHex = subject.colorHex,
            goalHoursText = subject.weeklyGoalHours.toString(),
            descriptionText = subject.description,
            nextTopic = subject.nextTopic,
            topicsJson = subject.topicsJson,
            semester = subject.semester
        )
    }

    fun updateSubjectName(name: String) {
        _subjectFormState.value = _subjectFormState.value.copy(nameText = name, nameError = null)
    }

    fun updateSubjectColor(colorHex: String) {
        _subjectFormState.value = _subjectFormState.value.copy(colorHex = colorHex)
    }

    fun updateSubjectGoalHours(goalHours: String) {
        _subjectFormState.value = _subjectFormState.value.copy(goalHoursText = goalHours, goalHoursError = null)
    }

    fun updateSubjectDescription(description: String) {
        _subjectFormState.value = _subjectFormState.value.copy(descriptionText = description)
    }

    fun updateSubjectNextTopic(subject: Subject, newNextTopic: String) {
        viewModelScope.launch {
            repository.updateSubject(subject.copy(nextTopic = newNextTopic))
            _snackbarEvent.emit("Active target topic set to '$newNextTopic'.")
        }
    }

    fun validateAndSaveSubject(onSuccess: () -> Unit) {
        val state = _subjectFormState.value
        var isValid = true
        var nameErr: String? = null
        var goalErr: String? = null

        if (state.nameText.isBlank()) {
            nameErr = "Subject name cannot be empty"
            isValid = false
        }

        val goalVal = state.goalHoursText.toDoubleOrNull()
        if (goalVal == null || goalVal <= 0.0) {
            goalErr = "Weekly goal must be a positive number"
            isValid = false
        } else if (goalVal > 100.0) {
            goalErr = "Weekly goal cannot exceed 100 hours"
            isValid = false
        }

        _subjectFormState.value = state.copy(
            nameError = nameErr,
            goalHoursError = goalErr
        )

        if (!isValid) return

        viewModelScope.launch {
            val subject = Subject(
                id = state.id,
                name = state.nameText.trim(),
                colorHex = state.colorHex,
                weeklyGoalHours = goalVal!!,
                description = state.descriptionText.trim(),
                nextTopic = state.nextTopic,
                topicsJson = state.topicsJson,
                semester = state.semester
            )

            if (state.id == 0) {
                repository.insertSubject(subject)
                _snackbarEvent.emit("Subject '${subject.name}' created.")
            } else {
                repository.updateSubject(subject)
                _snackbarEvent.emit("Subject '${subject.name}' updated.")
            }
            onSuccess()
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            _snackbarEvent.emit("Subject '${subject.name}' and its sessions deleted.")
        }
    }

    private fun <T> Flow<T>.asStateFlow(initialValue: T): StateFlow<T> {
        return this.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = initialValue
        )
    }
}
