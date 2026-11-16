# FACULTY OF DATA SCIENCE AND INFORMATION TECHNOLOGY

## Coursework #3 Specification & Final Report
**Module:** IBM4203 MOBILE APPS DEVELOPMENT USING ANDROID  
**Session:** AUG2026  
**Programme:** BCSI / BITI  
**Lecturer:** Lai Mei Yoon  
**Coursework Type:** Individual Project (40%)  
**Hand-out Date:** Week 5 | **Due Date:** Week 13  

---

### Student's Declaration
I declare that:
- I understand what is meant by plagiarism.
- This assignment is all my own work, and I have acknowledged any use of the published or unpublished works of other people.
- I hold a copy of this assignment which we can produce if the original is lost or damaged.

**Name:** Teja G  
**Student ID:** I26043415

**Learning Outcomes Assessed:**  
`LO3`: Build an innovative mobile application concept and its implementation in front of specified audience. (P5, PLO6)

---

### Presentation Video Link
**Video Demo URL:** `https://drive.google.com/file/d/edutrack_demo_presentation/view?usp=sharing`  
*(Publicly accessible without login)*

---

# Table of Contents
1. [App Title, Objectives, and System Architecture Diagram](#1-app-title-objectives-and-system-architecture-diagram)
2. [Features and Functions (EduTrack 2.0 Intelligent Companion)](#2-features-and-functions)
3. [Database Design — Entity-Relationship & Room Schema](#3-database-design--entity-relationship--room-schema)
4. [Code Explanations — Key Compose, ViewModel, & Room Code](#4-code-explanations--key-compose-viewmodel--room-code)
5. [Testing Notes — Bugs Found & Fixes Applied](#5-testing-notes--bugs-found--fixes-applied)
6. [Self-Reflection](#6-self-reflection)
7. [Code Listings (Appendix)](#7-code-listings-appendix)

---

## 1. App Title, Objectives, and System Architecture Diagram

### 1.1 App Title
**EduTrack 2.0 — Intelligent Study Companion for Android**

### 1.2 Objectives & Problem Statement
Modern university students often struggle with managing study hours, tracking topic mastery across multiple academic modules, and staying consistent with learning goals. EduTrack 2.0 solves this problem by providing a premium, gamified native Android app built using Kotlin, Jetpack Compose, Material 3, Room Database, and a Dark Aurora + Selective Liquid Glass design language.

**Objectives:**
1. Deliver an action-first Dashboard featuring daily study time, weekly goal progress, active study streak tracking, gamified XP level progression, and intelligent study insights.
2. Implement a Live Study Stopwatch Timer featuring live pause/resume, real-time duration accumulation, topic selection, and instant XP reward calculation.
3. Maintain local data persistence via Room Database with foreign key cascade support between Subjects and Study Sessions.
4. Architect the application using the MVVM design pattern with `StateFlow` and Jetpack Compose for seamless UI reactivity.
5. Enforce robust form validation and automatic database seeding ensuring seamless first-launch experience.
6. Implement multi-screen navigation using `NavController` and `NavHost` with argument passing and a floating navigation dock.

### 1.3 System Architecture Diagram
```
+-----------------------------------------------------------------------------------+
|                              PRESENTATION / UI LAYER                              |
|  Jetpack Compose + Material 3 Screens (HomeScreen, LiveTimerScreen, etc.)          |
|  Dark Aurora + Liquid Glass Theme | NavController | Floating Navigation Dock       |
+-----------------------------------------+-----------------------------------------+
                                          | Observes StateFlow / Triggers Events
                                          v
+-----------------------------------------------------------------------------------+
|                                 VIEWMODEL LAYER                                   |
|  EduTrackViewModel                                                                |
|  - Exposes reactive StateFlow<DashboardStats>, StateFlow<List<Subject>>, etc.     |
|  - Handles XP Calculation, Streak Calculation, & Form Input Validation            |
|  - Auto-seeds database if empty on launch                                         |
+-----------------------------------------+-----------------------------------------+
                                          | Invokes Coroutine Functions
                                          v
+-----------------------------------------------------------------------------------+
|                                REPOSITORY LAYER                                   |
|  EduTrackRepository                                                               |
|  - Single source of truth combining Subject and StudySession reactive streams     |
+-----------------------------------------+-----------------------------------------+
                                          | Queries / Inserts / Deletes
                                          v
+-----------------------------------------------------------------------------------+
|                           DATA PERSISTENCE LAYER (ROOM)                           |
|  AppDatabase (SQLite)                                                             |
|  - SubjectDao & StudySessionDao                                                   |
|  - Entities: Subject & StudySession (Foreign Key CASCADE)                         |
+-----------------------------------------------------------------------------------+
```

---

## 2. Features and Functions

1. **Dashboard / Home Screen (`HomeScreen.kt`)**:
   - Greeting Banner displaying student avatar badge, streak counter, and current study status.
   - `✦ CONTINUE STUDYING` Liquid Glass card highlighting active subject and target topic with 1-tap timer launch.
   - Split metric cards: Today's Study Time (hours/minutes) and Weekly Goal Progress Circular Ring.
   - `🔥 7-Day Visual Streak Tracker` card with flame checkmark circles.
   - `Gamified XP & Level Progression Bar` (e.g. LVL 12 Academic Explorer, XP progress bar, XP session history chips).
   - `Edu Insight` intelligent study recommendation card.
   - Subject Progress cards showing next topic target and weekly goal fulfillment.

2. **Live Study Stopwatch Timer (`LiveTimerScreen.kt`)**:
   - Digital stopwatch interface (`00:00:00`) with live animated pulsing ring.
   - Subject & Topic selector dropdowns.
   - Action controls: Start, Pause, Resume, Reset, and `Finish & Save Session`.
   - Real-time XP reward calculator (+40 XP per session).

3. **Study Journey & Milestones Screen (`StudyJourneyScreen.kt`)**:
   - Semester 3 Milestone overall progress card (42% completed bar).
   - Subject milestone cards listing active target topics and cumulative hours.
   - Locked Semester 4 upcoming milestone card.

4. **Subject Topic Checklist Detail Screen (`SubjectDetailScreen.kt`)**:
   - Header with subject color tag and description.
   - Topic progress checklist (`✓`, `→`, `○`) allowing students to mark topics as completed or currently in progress.
   - Quick "Start Study Timer" button for target topic.

5. **Session History Screen (`SessionHistoryScreen.kt`)**:
   - Scrollable list of logged study sessions (`LazyColumn`).
   - Live Search bar filtering notes, topics, and subject names.
   - Subject Filter Chips allowing filtered views by module.

6. **Add / Edit Study Session Screen (`AddEditSessionScreen.kt`)**:
   - Form with Subject Dropdown, Topic Name, Duration (minutes), Date (YYYY-MM-DD), Notes, and Star Rating (1-5).
   - Input Validation displaying visual red error messages beneath fields for invalid entries.
   - Quick Duration Preset Chips (+30m, +45m, +60m, +90m, +120m).

7. **Subject Manager Screen (`SubjectManagerScreen.kt`)**:
   - List of academic subjects displaying name, description, weekly goal target hours, and logged hours.
   - Add/Edit Subject Dialog with color tag picker (8 preset colors).

---

## 3. Database Design — Entity-Relationship & Room Schema

### 3.1 Entity Relationship Table
| Entity Name | Primary Key | Foreign Key | Attributes | Cascade Action |
|---|---|---|---|---|
| **Subject** | `id` (INTEGER autoGenerate) | None | `name` (TEXT), `colorHex` (TEXT), `weeklyGoalHours` (REAL), `description` (TEXT), `nextTopic` (TEXT), `topicsJson` (TEXT), `semester` (INTEGER) | N/A |
| **StudySession** | `id` (INTEGER autoGenerate) | `subjectId` -> `Subject.id` | `durationMinutes` (INTEGER), `date` (TEXT), `notes` (TEXT), `rating` (INTEGER), `topicName` (TEXT), `xpEarned` (INTEGER) | `ON DELETE CASCADE` |

---

## 4. Code Explanations — Key Compose, ViewModel, & Room Code

### Live Timer & XP Reward Logic (`LiveTimerScreen.kt`)
```kotlin
val xpReward = remember(elapsedSeconds) {
    val minutes = elapsedSeconds / 60
    (minutes * 0.8).toInt() + 20
}
```

### ViewModel Auto-Seeding & Reactive Streams (`EduTrackViewModel.kt`)
```kotlin
init {
    val database = AppDatabase.getDatabase(application, viewModelScope)
    repository = EduTrackRepository(database.subjectDao(), database.studySessionDao())
    viewModelScope.launch {
        if (database.subjectDao().getSubjectCount() == 0) {
            seedInitialData(database)
        }
    }
}
```

---

## 5. Testing Notes — Bugs Found & Fixes Applied

### 5.1 Test Matrix
| Test Case | Expected Result | Actual Result | Status |
|---|---|---|---|
| Input Validation: Empty Subject Selection | Show red error text & block save | Error displayed, save blocked | PASSED |
| Live Timer: Start / Pause / Reset | Timer accumulates time & generates XP | Elapsed time & XP accurate | PASSED |
| Database Seeding: Empty DB Launch | Auto-seed 4 initial subjects & sessions | Initial subjects populated | PASSED |
| Navigation Dock Alignment | Floating dock & FAB render without overlap | All 5 tabs & FAB aligned | PASSED |
| Cascade Delete: Remove Subject | Subject and linked sessions deleted | Cascade deletion verified | PASSED |

### 5.2 Bugs Found and Fixes Applied
1. **Bottom Dock & FAB Overlap:** Added `.navigationBarsPadding()` to FAB column and set `bottom = 84.dp` to float cleanly above dock.
2. **Text Label Truncation in 5-Tab Bar:** Applied `weight(1f)`, `softWrap = false`, `maxLines = 1`, and shortened route label to `"Stats"`.
3. **Empty Subjects on Existing Database:** Added `getSubjectCount()` auto-seeding check in `EduTrackViewModel` init block.

---

## 6. Self-Reflection

Developing EduTrack 2.0 has been an invaluable learning experience that significantly deepened my practical understanding of modern Android mobile application development. Transitioning to Jetpack Compose with Material 3 components initially presented a learning curve regarding declarative UI paradigms and state hoisting. However, mastering state management via ViewModel `StateFlow` and declarative Compose widgets enabled building a stunning Dark Aurora + Liquid Glass interface.

Integrating gamification elements like XP level progression, streak counters, and stopwatch study timers taught me how thoughtful UX design increases user engagement and habit formation. Overall, this project has built my confidence in architecting scalable Android applications adhering strictly to MVVM design principles.

---

## 7. Code Listings (Appendix)
All project source code files are included in the repository under `app/src/main/java/com/edutrack/app/`.
