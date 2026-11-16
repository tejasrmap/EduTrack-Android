package com.edutrack.gti.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.gti.data.local.StudySessionWithSubject
import com.edutrack.gti.data.local.Subject
import com.edutrack.gti.ui.theme.*

/**
 * Liquid Glass Card Component for high-priority cards with subtle gradient borders
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LiquidGlassFill,
    borderColor: Color = LiquidGlassBorderColor,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, borderColor.copy(alpha = 0.15f))
                ),
                shape = shape
            ),
        shape = shape,
        color = backgroundColor,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

/**
 * Circular Progress Ring Indicator for Weekly Progress
 */
@Composable
fun CircularProgressRing(
    progressPercent: Int,
    loggedHoursText: String,
    goalHoursText: String,
    modifier: Modifier = Modifier,
    ringColor: Color = ElectricIndigo
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (progressPercent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "WeeklyRingProgress"
    )

    Box(
        modifier = modifier.size(94.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 9.dp.toPx()
            drawArc(
                color = ringColor.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                ),
                color = Color.White
            )
            Text(
                text = "$loggedHoursText / $goalHoursText",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Continue Studying Liquid Glass Card
 */
@Composable
fun ContinueStudyingCard(
    sessionItem: StudySessionWithSubject?,
    subjects: List<Subject>,
    onContinueClick: (Subject?, String) -> Unit
) {
    val activeSubject = sessionItem?.subject ?: subjects.firstOrNull()
    val activeTopic = if (sessionItem?.session?.topicName?.isNotBlank() == true) {
        sessionItem.session.topicName
    } else activeSubject?.nextTopic ?: "Core Concepts"
    val durationText = "${sessionItem?.session?.durationMinutes ?: 45} min studied"

    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x2E1E1B4B),
        borderColor = Color(0x708B5CF6),
        shape = RoundedCornerShape(26.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ElectricIndigo)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "✦ CONTINUE STUDYING",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    fontWeight = FontWeight.ExtraBold,
                    color = ElectricIndigo
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Text(
                    text = "Next Up",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = activeSubject?.name ?: "Database Systems",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            ),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = activeTopic,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ElectricIndigo,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = durationText,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = { onContinueClick(activeSubject, activeTopic) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text("Continue", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

/**
 * 7-Day Visual Streak Tracker Component
 */
@Composable
fun StreakTracker7DayCard(
    streakDays: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MidnightBorder, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = StreakOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$streakDays DAY STREAK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
                val nextMilestone = ((streakDays / 5) + 1) * 5
                val remaining = nextMilestone - streakDays
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StreakOrange.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StreakOrange.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "$remaining day${if (remaining == 1) "" else "s"} to $nextMilestone 🔥",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StreakOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val days = listOf("M", "T", "W", "T", "F", "S", "S")
            val activeDaysCount = streakDays.coerceIn(0, 7)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEachIndexed { idx, dayLabel ->
                    val isActive = idx < activeDaysCount

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) StreakFlameGradient else Brush.linearGradient(
                                        listOf(MidnightSurfaceVariant, MidnightSurfaceVariant)
                                    )
                                )
                                .border(
                                    width = if (isActive) 0.dp else 1.dp,
                                    color = MidnightBorder,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isActive) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MidnightBorder)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Gamified XP & Level Progression Bar Component driven by Room StudySession XP
 */
@Composable
fun XpProgressBarCard(
    level: Int = 1,
    currentXp: Int = 0,
    maxXp: Int = 200,
    recentSessions: List<StudySessionWithSubject> = emptyList()
) {
    val progress = if (maxXp > 0) (currentXp.toFloat() / maxXp).coerceIn(0f, 1f) else 0f
    val rankTitle = when {
        level >= 10 -> "Master Scholar"
        level >= 5 -> "Academic Explorer"
        else -> "Rising Learner"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MidnightBorder, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RadiantViolet.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RadiantViolet.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "LVL $level • $rankTitle",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = RadiantViolet
                    )
                }
                Text(
                    text = "$currentXp / $maxXp XP",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = RadiantViolet,
                trackColor = MidnightSurfaceVariant
            )

            if (recentSessions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    recentSessions.take(2).forEach { item ->
                        val shortName = (item.subject?.name ?: "Study").split(" ").firstOrNull() ?: "Study"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.05f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                        ) {
                            Text(
                                text = "⚡ +${item.session.xpEarned} XP ($shortName, ${item.session.durationMinutes}m)",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Edu Insight Intelligent Recommendation Card (Liquid Glass)
 */
@Composable
fun EduInsightCard(
    insightText: String = "You focus longest between 7 PM – 9 PM. Database Systems is your most consistent subject this week."
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x1F06B6D4),
        borderColor = Color(0x6006B6D4),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "✦ EDU INSIGHT",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                fontWeight = FontWeight.ExtraBold,
                color = NeonCyan
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = insightText,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, fontWeight = FontWeight.Medium),
            color = Color.White
        )
    }
}

/**
 * Study Pulse Productivity Card Component
 */
@Composable
fun StudyPulseCard(
    pulsePercent: Int = 72,
    statusText: String = "Great pace (+18% vs last week)"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MidnightBorder, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreen.copy(alpha = 0.15f))
                    .border(1.5.dp, EmeraldGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$pulsePercent%",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = EmeraldGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "YOUR STUDY PULSE",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Global Session Card Component
 */
@Composable
fun ModernSessionCard(
    sessionItem: StudySessionWithSubject,
    onClick: () -> Unit
) {
    val session = sessionItem.session
    val subject = sessionItem.subject
    val color = parseHexColor(subject?.colorHex ?: "#6366F1")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MidnightBorder.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject?.name ?: "Unknown Subject",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (session.notes.isBlank()) "No notes recorded" else session.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = session.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = if (star <= session.rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = if (star <= session.rating) AmberGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.18f),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "${session.durationMinutes}m",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = color
                )
            }
        }
    }
}

/**
 * Visual Achievement Badges & Trophy Cabinet Component
 */
data class AchievementBadge(
    val id: String,
    val iconEmoji: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val color: Color
)

@Composable
fun TrophyCabinetCard(
    activeStreakDays: Int = 3,
    totalSessions: Int = 12,
    weeklyGoalPercent: Int = 0
) {
    val badges = remember(activeStreakDays, totalSessions, weeklyGoalPercent) {
        listOf(
            AchievementBadge("streak", "🔥", "Streak Builder", "Maintain a 3+ day study streak", activeStreakDays >= 3, StreakOrange),
            AchievementBadge("consistent", "⚡", "Active Logger", "Log 4+ study sessions", totalSessions >= 4, RadiantViolet),
            AchievementBadge("crusher", "🎯", "Goal Crusher", "Hit 100% of weekly goal", weeklyGoalPercent >= 100, EmeraldGreen),
            AchievementBadge("scholar", "📚", "Master Scholar", "Log 8+ study sessions", totalSessions >= 8, ElectricIndigo),
            AchievementBadge("week", "🏆", "7-Day Warrior", "Maintain a full 7-day streak", activeStreakDays >= 7, AmberGold)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MidnightBorder, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TROPHY CABINET",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AmberGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${badges.count { it.isUnlocked }}/${badges.size} Unlocked",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(badges) { badge ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (badge.isUnlocked) badge.color.copy(alpha = 0.12f) else MidnightSurfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (badge.isUnlocked) badge.color.copy(alpha = 0.4f) else MidnightBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (badge.isUnlocked) badge.iconEmoji else "🔒",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = badge.title,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (badge.isUnlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (badge.isUnlocked) "Unlocked" else "Locked",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (badge.isUnlocked) badge.color else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Exam Countdown & AI Study Plan Generator Card (Minimal & Clean Glass Style)
 */
@Composable
fun ExamCountdownCard(
    examName: String = "IBM4203 Mobile Apps Final Exam",
    daysRemaining: Int = 14,
    readinessPercent: Int = 72,
    subjects: List<Subject>
) {
    val totalGoal = remember(subjects) { subjects.sumOf { it.weeklyGoalHours }.coerceAtLeast(10.0) }
    val dailyPace = remember(totalGoal) { Math.round((totalGoal / 7.0) * 10) / 10.0 }
    val clampedReadiness = readinessPercent.coerceIn(0, 100)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MidnightBorder, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Title & Days Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⏳", fontSize = 16.sp)
                    Text(
                        text = "EXAM COUNTDOWN & AI PACE",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AmberGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$daysRemaining Days Left",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberGold
                    )
                }
            }

            // Main Title & Target Pace
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = examName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "AI Recommended Pace: ${dailyPace} hrs/day for 100% readiness",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Readiness Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LinearProgressIndicator(
                    progress = { clampedReadiness / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = ElectricIndigo,
                    trackColor = LiquidGlassFill
                )
                Text(
                    text = "$clampedReadiness% Ready",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElectricIndigo
                )
            }

            // Subject Breakdown Pills (Clean & Compact)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(subjects) { sub ->
                    val color = parseHexColor(sub.colorHex)
                    val reqHours = Math.round((sub.weeklyGoalHours / 7.0) * 10) / 10.0
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Text(
                            text = "${sub.name}: ${reqHours}h/d",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
