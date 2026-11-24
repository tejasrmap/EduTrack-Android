import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT

def append_arch_and_database(doc, helpers):
    add_styled_table, add_code_box, set_cell_props, set_table_borders = helpers

    def add_h1(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(18)
        p.paragraph_format.space_after = Pt(6)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(17)
        run.font.bold = True
        run.font.color.rgb = RGBColor(10, 25, 47)
        return p

    def add_h2(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(13)
        run.font.bold = True
        run.font.color.rgb = RGBColor(67, 56, 202)
        return p

    def add_h3(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(2)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(11.5)
        run.font.bold = True
        run.font.color.rgb = RGBColor(30, 41, 59)
        return p

    def add_body(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(5)
        p.paragraph_format.line_spacing = 1.15
        run = p.add_run(text)
        run.font.name = 'Times New Roman'
        run.font.size = Pt(11)
        run.font.color.rgb = RGBColor(15, 23, 42)
        return p

    def add_ascii_diagram(caption, diagram_text):
        add_h3(caption)
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(6)
        p.paragraph_format.line_spacing = 1.05
        run = p.add_run(diagram_text.strip())
        run.font.name = 'Courier New'
        run.font.size = Pt(8.0)
        run.font.color.rgb = RGBColor(30, 41, 59)

    print("Writing Chapter 4: System Architecture & Detailed Design...")
    add_h1("4.0 SYSTEM ARCHITECTURE & DETAILED DESIGN")
    
    add_h2("4.1 Multi-Tier Clean Architecture Decomposition")
    add_body(
        "EduTrack 2.0 strictly implements Clean Architecture combined with the official Google Android Architecture recommendations (MVVM with Unidirectional Data Flow). "
        "The system enforces strict unidirectional dependencies: the Presentation Layer depends solely on the Domain / ViewModel Layer, which in turn interacts "
        "with the Data Layer via abstract Repository interfaces. The underlying Room SQLite persistence mechanism is completely decoupled from the Composable UI, "
        "ensuring high testability, modularity, and maintainability."
    )

    clean_headers = ["Layer", "Core Package", "Key Classes & Responsibilities", "State & Data Flow Patterns"]
    clean_rows = [
        ["Presentation Layer", "com.edutrack.gti.ui.screens\ncom.edutrack.gti.ui.components", "HomeScreen, LiveTimerScreen, SessionHistoryScreen, SignUpScreen, UserProfileScreen, ModernSessionCard, CircularProgressRing.", "Observes StateFlow streams from ViewModel; emits user intents as lambda callbacks."],
        ["ViewModel / Domain Layer", "com.edutrack.gti.ui.viewmodel", "EduTrackViewModel, DashboardStats, SessionFormState, SubjectFormState, DateRangeFilter.", "Holds UI state across config changes; executes business logic (XP, streaks); launches coroutines on Dispatchers.IO."],
        ["Repository Layer", "com.edutrack.gti.data.repository", "EduTrackRepository", "Single source of truth; aggregates SubjectDao and StudySessionDao; exposes raw and joined Flow streams."],
        ["Local Persistence Layer", "com.edutrack.gti.data.local", "AppDatabase, SubjectDao, StudySessionDao, Subject, StudySession, StudySessionWithSubject.", "Executes SQLite transactions; maintains foreign key referential integrity; manages database versioning and deduplication."]
    ]
    add_styled_table(clean_headers, clean_rows, [1.5, 1.8, 2.3, 1.6])

    add_h2("4.2 Presentation Layer: Jetpack Compose Declarative Tree & Recomposition")
    add_body(
        "Jetpack Compose transforms traditional imperative Android UI development by modeling screens as pure functions of state. "
        "In EduTrack 2.0, composables are stateless wherever possible (State Hoisting), delegating state persistence to the ViewModel. "
        "To guarantee 60 FPS scrolling performance, recomposition scopes are strictly isolated:\n"
        "• Smart Recomposition: Stable data models (data classes with immutable val properties) enable Compose runtime to skip redundant recompositions.\n"
        "• Derived State: Heavy filtering operations (such as the multi-criteria search in SessionHistoryScreen) use derivedStateOf to prevent re-filtering during unrelated recompositions.\n"
        "• Lazy Layout Keys: LazyColumn items supply explicit unique keys (e.g. key = { it.id }), allowing the layout engine to preserve scroll position and animate list reordering efficiently."
    )

    add_h2("4.3 Comprehensive Interaction Sequence Diagrams (10 Core Lifecycles)")
    add_body(
        "Ten complete end-to-end interaction lifecycles are formally documented below using UML Sequence representations:"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.1: Multi-Step Student Registration & BackHandler Flow",
        "Student                 SignUpScreen (Compose)             BackHandler                  EduTrackViewModel            SharedPreferences\n"
        "   |                               |                            |                               |                            |\n"
        "   |--- Enter Step 1 Details ----->|                            |                               |                            |\n"
        "   |--- Tap 'Next: Academics' ---->|                            |                               |                            |\n"
        "   |                               |--- currentStep = 2 ------->|                               |                            |\n"
        "   |                               |                            |--- BackHandler(enabled=true)->|                            |\n"
        "   |--- Press Hardware Back ------>|                            |                               |                            |\n"
        "   |                               |<-- onBack() intercepted --|                               |                            |\n"
        "   |                               |--- currentStep = 1 ------->|                               |                            |\n"
        "   |--- Re-enter & Advance to 3 -->|                            |                               |                            |\n"
        "   |--- Tap 'Complete Sign Up' --->|                            |                               |                            |\n"
        "   |                               |---------------------------- register(credentials) -------->|                            |\n"
        "   |                               |                                                            |--- persist student data -->|\n"
        "   |                               |                                                            |<-- commit ok --------------|\n"
        "   |                               |<-- navigate(HomeScreen) { popUpTo(Login) { inclusive } } --|                            |\n"
        "   |                               |                                                            |                            |\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.2: Student Login & Cockpit Route Invalidation Flow",
        "Student                 LoginScreen (Compose)              EduTrackViewModel            SharedPreferences            NavController\n"
        "   |                               |                               |                            |                            |\n"
        "   |--- Enter Email & Password --->|                               |                            |                            |\n"
        "   |--- Tap 'Sign In' ------------>|                               |                            |                            |\n"
        "   |                               |--- login(email, pass) ------->|                            |                            |\n"
        "   |                               |                               |--- getStoredCredentials() ->|                            |\n"
        "   |                               |                               |<-- return credentials -----|                            |\n"
        "   |                               |                               |--- validate match: TRUE    |                            |\n"
        "   |                               |                               |--- putBoolean(is_logged_in)|                            |\n"
        "   |                               |<-- return success = true -----|                            |                            |\n"
        "   |                               |------------------------------- onLoginSuccess() --------------------------------------->|\n"
        "   |                               |                                                                                         |--- navigate(Home)\n"
        "   |                               |                                                                                         |    popUpTo(Login)\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.3: Home Cockpit Cold Start & Reactive Aggregation Flow",
        "NavController           HomeScreen                         EduTrackViewModel            EduTrackRepository           AppDatabase (SQLite)\n"
        "   |                         |                                     |                            |                                 |\n"
        "   |--- Route to Home ------>|                                     |                            |                                 |\n"
        "   |                         |--- collectAsState(dashboardStats) ->|                            |                                 |\n"
        "   |                         |--- collectAsState(sessions) ------->|                            |                                 |\n"
        "   |                         |                                     |--- getSessionsWithSub() -->|                                 |\n"
        "   |                         |                                     |                            |--- @Transaction Flow query ---->|\n"
        "   |                         |                                     |                            |<-- emit List<SessionWithSub> ---|\n"
        "   |                         |                                     |<-- Flow emission ----------|                                 |\n"
        "   |                         |                                     |--- compute today & weekly  |                                 |\n"
        "   |                         |<-- emit updated DashboardStats -----|                                                              |\n"
        "   |                         |--- Render Progress Ring & Cards --->|                                                              |\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.4: Live Focus Stopwatch Study Session & Audio Synthesis Flow",
        "Student                 LiveTimerScreen (Compose)          AmbientSoundPlayer           EduTrackViewModel            EduTrackRepository\n"
        "   |                               |                               |                            |                            |\n"
        "   |--- Select 'Rain' Sound ------>|                               |                            |                            |\n"
        "   |                               |--- playSound('Rain') -------->|                            |                            |\n"
        "   |                               |                               |--- AudioTrack.play() ----->|                            |\n"
        "   |--- Tap 'Start Session' ------>|                               |                            |                            |\n"
        "   |                               |--- coroutine ticker (1s)      |                            |                            |\n"
        "   |--- Tap 'Stop Session' ------->|                               |                            |                            |\n"
        "   |                               |--- calculate XP: (m*0.8)+20   |                            |                            |\n"
        "   |--- Tap 'Save & Claim XP' ---->|                               |                            |                            |\n"
        "   |                               |--- logCompletedTimerSession() ---------------------------->|                            |\n"
        "   |                               |                                                            |--- insertSession() ------->|\n"
        "   |                               |                                                            |<-- SQLite rowId -----------|\n"
        "   |                               |--- onDispose { stopSound() } >|                            |                            |\n"
        "   |                               |<-- navigateBack() ------------|                            |                            |\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.5: Manual Study Session Form Logging & Validation Flow",
        "Student                 AddEditSessionScreen               EduTrackViewModel            EduTrackRepository           SnackbarHost\n"
        "   |                               |                               |                            |                            |\n"
        "   |--- Select Subject & Date ---->|                               |                            |                            |\n"
        "   |--- Enter Duration: '60' ----->|                               |                            |                            |\n"
        "   |--- Tap 'Save Session' ------->|                               |                            |                            |\n"
        "   |                               |--- saveSessionForm() -------->|                            |                            |\n"
        "   |                               |                               |--- validate inputs: OK     |                            |\n"
        "   |                               |                               |--- calculate XP = 68       |                            |\n"
        "   |                               |                               |--- insertSession(session) >|                            |\n"
        "   |                               |                               |                            |--- SQLite INSERT --------->|\n"
        "   |                               |                               |--- emit snackbar banner ------------------------------->|\n"
        "   |                               |<-- onSuccess: popBackStack() -|                                                         |\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.6: Cascade Subject Deletion & Referential Purge Flow",
        "Student                 SubjectManagerScreen               AlertDialog                  EduTrackViewModel            AppDatabase (SQLite)\n"
        "   |                               |                            |                               |                            |\n"
        "   |--- Tap Delete Icon ---------->|                            |                               |                            |\n"
        "   |                               |--- showDeleteDialog() ---->|                               |                            |\n"
        "   |                               |<-- render confirmation ----|                               |                            |\n"
        "   |--- Confirm 'Delete' --------->|                            |                               |                            |\n"
        "   |                               |---------------------------- deleteSubject(subject) ------->|                            |\n"
        "   |                               |                                                            |--- DELETE FROM subjects -->|\n"
        "   |                               |                                                            |    WHERE id = :id          |\n"
        "   |                               |                                                            |    (ON DELETE CASCADE)     |\n"
        "   |                               |                                                            |    DELETE child sessions   |\n"
        "   |                               |                                                            |<-- cascade complete -------|\n"
        "   |                               |<-- UI updates via Room Flow -------------------------------|                            |\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.7: Multi-Criteria Session History Search & Filtering Flow",
        "Student                 SessionHistoryScreen               EduTrackViewModel            DerivedState                 FilteredStateFlow\n"
        "   |                               |                               |                            |                            |\n"
        "   |--- Type 'Algorithms' -------->|                               |                            |                            |\n"
        "   |                               |--- setSearchQuery('Algs') --->|                            |                            |\n"
        "   |--- Tap Date Filter 'This Week'|                               |                            |                            |\n"
        "   |                               |--- setDateRangeFilter(WEEK) ->|                            |                            |\n"
        "   |                               |                               |--- combine(query, date) -->|                            |\n"
        "   |                               |                               |                            |--- execute token regex --->|\n"
        "   |                               |                               |                            |<-- return filtered list ---|\n"
        "   |                               |<-- emit filteredSessions -----|----------------------------|                            |\n"
        "   |                               |--- LazyColumn diff & render ->|                                                         |\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.8: Student Profile Editing & Parameter Propagation Flow",
        "Student                 UserProfileScreen (Edit Mode)      EduTrackViewModel            SharedPreferences            StudyJourneyScreen\n"
        "   |                               |                               |                            |                            |\n"
        "   |--- Edit Target Hours: 30.0 -->|                               |                            |                            |\n"
        "   |--- Select Semester: S4 ------>|                               |                            |                            |\n"
        "   |--- Tap 'Save Changes' ------->|                               |                            |                            |\n"
        "   |                               |--- updateUserProfile() ------>|                            |                            |\n"
        "   |                               |                               |--- coerceIn(1.0, 168.0)    |                            |\n"
        "   |                               |                               |--- persist to prefs ------>|                            |\n"
        "   |                               |                               |--- update _currentSemester |                            |\n"
        "   |                               |                               |<-- emit updated state -----|                            |\n"
        "   |                               |<-- exit edit mode ------------|                                                         |\n"
        "   |                               |                               |--- notify semester change ----------------------------->|\n"
        "   |                               |                               |                                                         |--- sync S4 roadmap\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.9: Study Journey Syllabus Topic Checklist Toggle Flow",
        "Student                 StudyJourneyScreen                 EduTrackViewModel            Subject (topicsJson)         AppDatabase\n"
        "   |                               |                               |                            |                            |\n"
        "   |--- Tap Topic Checkbox ------->|                               |                            |                            |\n"
        "   |    (e.g. 'Binary Trees')      |                               |                            |                            |\n"
        "   |                               |--- toggleTopicCompleted() --->|                            |                            |\n"
        "   |                               |                               |--- update JSON array string|                            |\n"
        "   |                               |                               |--- updateSubject(subject) >|                            |\n"
        "   |                               |                               |                            |--- SQLite UPDATE --------->|\n"
        "   |                               |                               |<-- Flow emission ----------|                            |\n"
        "   |                               |<-- re-render checklist state -|                                                         |\n"
        "   |                               |--- progress bar animates +25% |                                                         |\n"
    )

    add_ascii_diagram(
        "Sequence Diagram 4.10: Student Account Logout & Backstack Purge Flow",
        "Student                 UserProfileScreen                  EduTrackViewModel            SharedPreferences            NavController\n"
        "   |                               |                               |                            |                            |\n"
        "   |--- Tap 'Log Out of Account' ->|                               |                            |                            |\n"
        "   |                               |--- logout() ----------------->|                            |                            |\n"
        "   |                               |                               |--- putBoolean(is_logged_in)|                            |\n"
        "   |                               |                               |<-- committed --------------|                            |\n"
        "   |                               |                               |--- emit snackbar event ----|                            |\n"
        "   |                               |------------------------------- onLogout() --------------------------------------------->|\n"
        "   |                               |                                                                                         |--- navigate(Login)\n"
        "   |                               |                                                                                         |    popUpTo(0) {\n"
        "   |                               |                                                                                         |      inclusive=true\n"
        "   |                               |                                                                                         |    }\n"
    )

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 5: DATABASE ARCHITECTURE & RELATIONAL MODELING
    # =========================================================================
    print("Writing Chapter 5: Database Architecture & Relational Modeling...")
    add_h1("5.0 DATABASE ARCHITECTURE & RELATIONAL MODELING")
    
    add_h2("5.1 Relational Entity Specifications (Subject & StudySession)")
    add_body(
        "EduTrack 2.0 structures local academic persistence around two primary relational entities: Subject and StudySession, "
        "interlinked via a one-to-many relationship with strict foreign key constraints and cascade rules."
    )

    add_h3("Data Dictionary 5.1: Table 'subjects'")
    sub_dd_headers = ["Column Name", "SQLite Type", "Constraints", "Default Value", "Description"]
    sub_dd_rows = [
        ["id", "INTEGER", "PRIMARY KEY AUTOINCREMENT, NOT NULL", "None", "Unique internal surrogate identifier for academic subject."],
        ["name", "TEXT", "NOT NULL, INDEXED", "None", "Official module name (e.g. 'Data Structures & Algorithms')."],
        ["colorHex", "TEXT", "NOT NULL", "'#6366F1'", "Hexadecimal color token for visual tag rendering (e.g. '#10B981')."],
        ["weeklyGoalHours", "REAL", "NOT NULL", "5.0", "Target study quota in hours allocated to this module per week."],
        ["description", "TEXT", "NOT NULL", "''", "Extended curriculum summary or syllabus learning outcomes."],
        ["nextTopic", "TEXT", "NOT NULL", "'Introduction'", "Next recommended curricular topic scheduled for revision."],
        ["topicsJson", "TEXT", "NOT NULL", "'[\"Intro\"]'", "Serialized JSON array containing module syllabus topic checklist items."],
        ["semester", "INTEGER", "NOT NULL", "3", "Academic semester tier (1–6) binding subject to Study Journey roadmap."]
    ]
    add_styled_table(sub_dd_headers, sub_dd_rows, [1.2, 1.1, 1.8, 1.0, 2.1])

    add_h3("Data Dictionary 5.2: Table 'study_sessions'")
    sess_dd_headers = ["Column Name", "SQLite Type", "Constraints", "Default Value", "Description"]
    sess_dd_rows = [
        ["id", "INTEGER", "PRIMARY KEY AUTOINCREMENT, NOT NULL", "None", "Unique surrogate identifier for individual study session."],
        ["subjectId", "INTEGER", "FOREIGN KEY -> subjects(id) ON DELETE CASCADE, INDEXED, NOT NULL", "None", "Foreign key binding session to parent subject."],
        ["durationMinutes", "INTEGER", "NOT NULL", "0", "Net duration studied in minutes (must be >= 1)."],
        ["date", "TEXT", "NOT NULL, INDEXED", "None", "ISO-8601 calendar date string (YYYY-MM-DD) indicating when study occurred."],
        ["notes", "TEXT", "NOT NULL", "''", "Qualitative student reflection, problem solutions, or summary notes."],
        ["rating", "INTEGER", "NOT NULL", "5", "Self-assessed session focus/satisfaction rating (1–5 stars)."],
        ["topicName", "TEXT", "NOT NULL", "'General Study'", "Specific modular topic studied during this focus interval."],
        ["xpEarned", "INTEGER", "NOT NULL", "20", "Gamification experience points computed via (Minutes * 0.8) + 20."]
    ]
    add_styled_table(sess_dd_headers, sess_dd_rows, [1.2, 1.1, 2.0, 1.0, 1.9])

    add_h2("5.2 Complete Database DDL SQL Scripts")
    add_body(
        "The complete SQLite Data Definition Language (DDL) generated by the Room compiler is documented below:"
    )

    sql_ddl = (
        "-- Table: subjects\n"
        "CREATE TABLE IF NOT EXISTS `subjects` (\n"
        "    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
        "    `name` TEXT NOT NULL,\n"
        "    `colorHex` TEXT NOT NULL,\n"
        "    `weeklyGoalHours` REAL NOT NULL,\n"
        "    `description` TEXT NOT NULL,\n"
        "    `nextTopic` TEXT NOT NULL,\n"
        "    `topicsJson` TEXT NOT NULL,\n"
        "    `semester` INTEGER NOT NULL\n"
        ");\n\n"
        "-- Indices for subjects\n"
        "CREATE INDEX IF NOT EXISTS `index_subjects_name` ON `subjects` (`name`);\n\n"
        "-- Table: study_sessions\n"
        "CREATE TABLE IF NOT EXISTS `study_sessions` (\n"
        "    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
        "    `subjectId` INTEGER NOT NULL,\n"
        "    `durationMinutes` INTEGER NOT NULL,\n"
        "    `date` TEXT NOT NULL,\n"
        "    `notes` TEXT NOT NULL,\n"
        "    `rating` INTEGER NOT NULL,\n"
        "    `topicName` TEXT NOT NULL,\n"
        "    `xpEarned` INTEGER NOT NULL,\n"
        "    FOREIGN KEY(`subjectId`) REFERENCES `subjects`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE\n"
        ");\n\n"
        "-- Indices for study_sessions\n"
        "CREATE INDEX IF NOT EXISTS `index_study_sessions_subjectId` ON `study_sessions` (`subjectId`);\n"
        "CREATE INDEX IF NOT EXISTS `index_study_sessions_date` ON `study_sessions` (`date`);"
    )
    add_code_box(sql_ddl)

    add_h2("5.3 Room DAO Interfaces & Transaction Relational Queries")
    add_body(
        "StudySessionDao employs Room's @Transaction annotation to perform relational joins without manual SQLite cursor parsing. "
        "The StudySessionWithSubject projection embeds the parent Subject entity inside each StudySession, providing full subject styling "
        "to every historical card with zero N+1 query overhead:"
    )

    dao_code = (
        "@Dao\n"
        "interface StudySessionDao {\n"
        "    @Transaction\n"
        "    @Query(\"\"\"\n"
        "        SELECT * FROM study_sessions\n"
        "        ORDER BY date DESC, id DESC\n"
        "    \"\"\")\n"
        "    fun getAllSessionsWithSubject(): Flow<List<StudySessionWithSubject>>\n\n"
        "    @Query(\"\"\"\n"
        "        SELECT IFNULL(SUM(durationMinutes), 0) FROM study_sessions\n"
        "        WHERE date = :today\n"
        "    \"\"\")\n"
        "    fun getTotalMinutesToday(today: String): Flow<Int>\n\n"
        "    @Query(\"\"\"\n"
        "        SELECT IFNULL(SUM(durationMinutes), 0) FROM study_sessions\n"
        "        WHERE date >= :startDate\n"
        "    \"\"\")\n"
        "    fun getTotalMinutesSince(startDate: String): Flow<Int>\n\n"
        "    @Insert(onConflict = OnConflictStrategy.REPLACE)\n"
        "    suspend fun insertSession(session: StudySession): Long\n\n"
        "    @Update\n"
        "    suspend fun updateSession(session: StudySession)\n\n"
        "    @Delete\n"
        "    suspend fun deleteSession(session: StudySession)\n"
        "}"
    )
    add_code_box(dao_code)

    doc.add_page_break()
    print("Checkpoint 2: Chapters 4 & 5 saved successfully.")
