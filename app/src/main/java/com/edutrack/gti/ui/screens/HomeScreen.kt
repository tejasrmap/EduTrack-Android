package com.edutrack.gti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.gti.data.local.Subject
import com.edutrack.gti.ui.components.*
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel

@Composable
fun HomeScreen(
    viewModel: EduTrackViewModel,
    onContinueStudying: (Subject?, String) -> Unit,
    onNavigateToSubjectManager: () -> Unit,
    onNavigateToSessionDetail: (Int) -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val sessions by viewModel.sessionsWithSubject.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val weeklyMinutesBySubject by viewModel.weeklyMinutesBySubject.collectAsState()
    val userName by viewModel.userName.collectAsState()

// Profile state managed in UserProfileScreen

    val userInitials = remember(userName) {
        userName.trim().split("\\s+".toRegex()).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2).ifEmpty { "TG" }
    }

    val lastSessionItem = sessions.firstOrNull()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkAuroraBg)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 180.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Greeting Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.clickable { onNavigateToProfile() }
                    ) {
                        Text(
                            text = "Hello, $userName 👋",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StreakOrange.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "🔥 ${stats.activeStreakDays} day streak",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = StreakOrange
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View student profile",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Gradient Avatar Pill (Clickable to Edit Name)
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(HeroAuroraGradient)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(MidnightSurface)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userInitials,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // 2. ✦ CONTINUE STUDYING Card (Liquid Glass)
            item {
                ContinueStudyingCard(
                    sessionItem = lastSessionItem,
                    subjects = subjects,
                    onContinueClick = { sub, topic -> onContinueStudying(sub, topic) }
                )
            }

            // 3. TODAY & WEEKLY PROGRESS SPLIT SECTION
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Today's Study Card
                    Card(
                        modifier = Modifier
                            .weight(1.1f)
                            .height(130.dp)
                            .border(1.dp, MidnightBorder, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TODAY",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldGreen.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = "${stats.totalMinutesToday}m",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldGreen
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "${stats.totalMinutesToday / 60}h ${stats.totalMinutesToday % 60}m",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 28.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = "studied today",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Weekly Circular Progress Ring Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .border(1.dp, MidnightBorder, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressRing(
                                progressPercent = stats.weeklyGoalProgressPercent,
                                loggedHoursText = "${stats.totalHoursThisWeek}h",
                                goalHoursText = "${stats.totalWeeklyGoalHours}h"
                            )
                        }
                    }
                }
            }

            // 4. SUBJECTS PROGRESS & RECENT ACTIVITY LIST
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SUBJECTS & PROGRESS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            fontSize = 15.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        modifier = Modifier.clickable { onNavigateToSubjectManager() },
                        shape = RoundedCornerShape(12.dp),
                        color = ElectricIndigo.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricIndigo.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Manage",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ElectricIndigo,
                                softWrap = false,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = ElectricIndigo,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            items(subjects, key = { it.id }) { subject ->
                val subjectWeeklyMinutes = weeklyMinutesBySubject[subject.id] ?: 0
                val subjectWeeklyHours = Math.round((subjectWeeklyMinutes / 60.0) * 10) / 10.0
                val recentSubSession = sessions.firstOrNull { it.session.subjectId == subject.id }?.session
                val color = parseHexColor(subject.colorHex)

                val progress = if (subject.weeklyGoalHours > 0) {
                    ((subjectWeeklyHours / subject.weeklyGoalHours) * 100).coerceIn(0.0, 100.0).toInt()
                } else 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MidnightBorder.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                        .clickable { onContinueStudying(subject, subject.nextTopic) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Text(
                                text = "$progress%",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = color
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${subjectWeeklyHours}h / ${subject.weeklyGoalHours}h this week",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (recentSubSession != null) {
                                    "Recent: ${recentSubSession.durationMinutes}m (${recentSubSession.date})"
                                } else {
                                    "No recent sessions"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = color,
                            trackColor = color.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Next: ${subject.nextTopic}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Start Timer",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = color
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. RECENT STUDY ACTIVITY (Tap to open Session Detail)
            if (sessions.isNotEmpty()) {
                item {
                    Text(
                        text = "RECENT STUDY ACTIVITY",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = Color.White
                    )
                }

                items(sessions.take(3), key = { "recent_${it.session.id}" }) { sessionItem ->
                    ModernSessionCard(
                        sessionItem = sessionItem,
                        onClick = { onNavigateToSessionDetail(sessionItem.session.id) }
                    )
                }
            }

            // 6. 🔥 7-DAY STREAK TRACKER
            item {
                StreakTracker7DayCard(streakDays = stats.activeStreakDays)
            }

            // 7. GAMIFIED XP & LEVEL PROGRESSION BAR (Driven by Room XP)
            item {
                XpProgressBarCard(
                    level = stats.level,
                    currentXp = stats.currentLevelXp,
                    maxXp = stats.maxLevelXp,
                    recentSessions = sessions
                )
            }

            // 8. VISUAL ACHIEVEMENT BADGES & TROPHY CABINET
            item {
                TrophyCabinetCard(
                    activeStreakDays = stats.activeStreakDays,
                    totalSessions = stats.totalSessionsCount,
                    weeklyGoalPercent = stats.weeklyGoalProgressPercent
                )
            }

            // 9. 🎯 EXAM COUNTDOWN & AI STUDY PLAN CARD
            item {
                ExamCountdownCard(
                    examName = "IBM4203 Mobile Apps Final Exam",
                    daysRemaining = 14,
                    readinessPercent = stats.weeklyGoalProgressPercent.coerceAtLeast(15),
                    subjects = subjects
                )
            }

            // 10. ✦ EDU INSIGHT CARD (Liquid Glass)
            item {
                EduInsightCard(
                    insightText = "Your strongest study day is ${stats.bestStudyDayName} (${stats.bestStudyDayDurationText}). ${stats.mostStudiedSubjectName} leads your logged study hours (${stats.mostStudiedSubjectHoursText})."
                )
            }
        }


    }
}
