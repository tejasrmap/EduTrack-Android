package com.edutrack.gti.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Database(
    entities = [Subject::class, StudySession::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val seedMutex = Mutex()

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edutrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Cleans up any duplicate subjects by case-insensitive name,
         * safely reassigning their foreign key sessions to the primary subject.
         */
        suspend fun deduplicateSubjects(database: AppDatabase) {
            withContext(Dispatchers.IO) {
                val subjectDao = database.subjectDao()
                val sessionDao = database.studySessionDao()
                val allSubjects = subjectDao.getAllSubjectsOneShot()
                val grouped = allSubjects.groupBy { it.name.trim().lowercase() }
                for ((_, list) in grouped) {
                    if (list.size > 1) {
                        val primary = list.first()
                        val duplicates = list.drop(1)
                        for (dup in duplicates) {
                            sessionDao.reassignSessionsSubject(dup.id, primary.id)
                            subjectDao.deleteSubject(dup)
                        }
                    }
                }
            }
        }

        /**
         * Thread-safe single-source initial data seeder protected by a Mutex
         * to prevent duplicate subjects/sessions on first install.
         */
        suspend fun seedInitialDataIfEmpty(database: AppDatabase) {
            seedMutex.withLock {
                withContext(Dispatchers.IO) {
                    val subjectDao = database.subjectDao()
                    val sessionDao = database.studySessionDao()
                    if (subjectDao.getSubjectCount() > 0) {
                        deduplicateSubjects(database)
                        return@withContext
                    }

                    val s1 = Subject(
                        name = "Database Systems",
                        colorHex = "#0D9488", // Teal
                        weeklyGoalHours = 6.0,
                        description = "Relational Databases & SQL Queries",
                        nextTopic = "Normalization",
                        topicsJson = "[\"ER Diagrams\", \"Keys & Constraints\", \"Normalization\", \"SQL Joins & Indexing\", \"Transactions\"]",
                        semester = 3
                    )

                    val s2 = Subject(
                        name = "Data Structures & Algorithms",
                        colorHex = "#F59E0B", // Amber / Orange
                        weeklyGoalHours = 7.0,
                        description = "Complexity & Graph Theory",
                        nextTopic = "Trees & Graphs",
                        topicsJson = "[\"Arrays & Linked Lists\", \"Stacks & Queues\", \"Trees & Graphs\", \"Dynamic Programming\", \"Sort Algorithms\"]",
                        semester = 3
                    )

                    val s3 = Subject(
                        name = "Mobile Apps Development",
                        colorHex = "#6366F1", // Indigo
                        weeklyGoalHours = 8.0,
                        description = "IBM4203 - Jetpack Compose & Android Studio",
                        nextTopic = "State Management & Room DB",
                        topicsJson = "[\"Kotlin & Compose Basics\", \"UI Composables\", \"State Management & Room DB\", \"MVVM Architecture\", \"Testing & Deploy\"]",
                        semester = 3
                    )

                    val s4 = Subject(
                        name = "Software Engineering",
                        colorHex = "#8B5CF6", // Violet
                        weeklyGoalHours = 5.0,
                        description = "Agile, Clean Architecture & Testing",
                        nextTopic = "CI/CD & Pipeline",
                        topicsJson = "[\"Agile & Scrum\", \"Requirements Eng\", \"Clean Architecture\", \"CI/CD & Pipeline\", \"System Security\"]",
                        semester = 3
                    )

                    val id1 = subjectDao.insertSubject(s1).toInt()
                    val id2 = subjectDao.insertSubject(s2).toInt()
                    val id3 = subjectDao.insertSubject(s3).toInt()
                    val id4 = subjectDao.insertSubject(s4).toInt()

                    val today = LocalDate.now()
                    val yesterday = today.minusDays(1)
                    val twoDaysAgo = today.minusDays(2)
                    val threeDaysAgo = today.minusDays(3)

                    sessionDao.insertSession(
                        StudySession(
                            subjectId = id1,
                            durationMinutes = 45,
                            date = today.toString(),
                            notes = "Completed ER Diagram models and foreign key constraints.",
                            rating = 5,
                            topicName = "ER Diagrams & Foreign Keys",
                            xpEarned = 56
                        )
                    )

                    sessionDao.insertSession(
                        StudySession(
                            subjectId = id3,
                            durationMinutes = 90,
                            date = today.toString(),
                            notes = "Built Jetpack Compose state flows and Room DAO repository layers.",
                            rating = 5,
                            topicName = "State Management & Room DB",
                            xpEarned = 92
                        )
                    )

                    sessionDao.insertSession(
                        StudySession(
                            subjectId = id2,
                            durationMinutes = 60,
                            date = yesterday.toString(),
                            notes = "Solved Binary Search Tree & Graph traversal algorithms.",
                            rating = 4,
                            topicName = "Trees & Graphs",
                            xpEarned = 68
                        )
                    )

                    sessionDao.insertSession(
                        StudySession(
                            subjectId = id1,
                            durationMinutes = 75,
                            date = yesterday.toString(),
                            notes = "Reviewed 1NF, 2NF, and 3NF normalization rules.",
                            rating = 4,
                            topicName = "Normalization",
                            xpEarned = 80
                        )
                    )

                    sessionDao.insertSession(
                        StudySession(
                            subjectId = id4,
                            durationMinutes = 45,
                            date = twoDaysAgo.toString(),
                            notes = "Read MVVM architecture pattern documentation.",
                            rating = 4,
                            topicName = "Clean Architecture",
                            xpEarned = 56
                        )
                    )

                    sessionDao.insertSession(
                        StudySession(
                            subjectId = id2,
                            durationMinutes = 90,
                            date = threeDaysAgo.toString(),
                            notes = "Implemented Dijkstra and BFS shortest-path practice problems.",
                            rating = 5,
                            topicName = "Dynamic Programming",
                            xpEarned = 92
                        )
                    )
                }
            }
        }
    }
}
