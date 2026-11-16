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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.gti.ui.components.ModernSessionCard
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    viewModel: EduTrackViewModel,
    subjectId: Int,
    onStartTimer: (Int, String) -> Unit,
    onNavigateToSessionDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val sessions by viewModel.sessionsWithSubject.collectAsState()
    val weeklyMinutesBySubject by viewModel.weeklyMinutesBySubject.collectAsState()

    val subject = subjects.find { it.id == subjectId }
    val subjectSessions = sessions.filter { it.session.subjectId == subjectId }
    val color = parseHexColor(subject?.colorHex ?: "#6366F1")

    val weeklyMinutes = weeklyMinutesBySubject[subjectId] ?: 0
    val weeklyHours = Math.round((weeklyMinutes / 60.0) * 10) / 10.0
    val progressPercent = if ((subject?.weeklyGoalHours ?: 0.0) > 0) {
        ((weeklyHours / subject!!.weeklyGoalHours) * 100).coerceIn(0.0, 100.0).toInt()
    } else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subject?.name ?: "Subject Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkAuroraBg)
            )
        },
        containerColor = DarkAuroraBg
    ) { innerPadding ->
        if (subject == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Subject not found.")
            }
        } else {
            val parsedTopics = remember(subject.topicsJson, subject.nextTopic) {
                val list = mutableListOf<String>()
                try {
                    val jsonArr = JSONArray(subject.topicsJson)
                    for (i in 0 until jsonArr.length()) {
                        list.add(jsonArr.getString(i))
                    }
                } catch (e: Exception) {
                    // Fallback if malformed JSON
                }
                if (list.isEmpty()) {
                    list.addAll(listOf("Introduction", subject.nextTopic, "Core Practice", "Exam Review").distinct())
                }
                list
            }

            val activeIndex = parsedTopics.indexOf(subject.nextTopic).let { if (it >= 0) it else 0 }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = color
                                )
                            }
                            if (subject.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = subject.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Weekly Progress", style = MaterialTheme.typography.bodySmall)
                                Text("${weeklyHours}h / ${subject.weeklyGoalHours}h ($progressPercent%)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = color)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progressPercent / 100f },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = color,
                                trackColor = color.copy(alpha = 0.2f)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Topic Milestones (Tap to set active target)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, MidnightBorder, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            parsedTopics.forEachIndexed { idx, topicName ->
                                val isCompleted = idx < activeIndex
                                val isActive = idx == activeIndex

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.updateSubjectNextTopic(subject, topicName) }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isCompleted -> EmeraldGreen.copy(alpha = 0.2f)
                                                        isActive -> ElectricIndigo.copy(alpha = 0.25f)
                                                        else -> MidnightSurfaceVariant
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            when {
                                                isCompleted -> Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                                                isActive -> Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ElectricIndigo))
                                                else -> Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MidnightBorder))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = topicName,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    if (isActive) {
                                        IconButton(onClick = { onStartTimer(subject.id, topicName) }) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Start Topic Timer", tint = ElectricIndigo)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Study History (${subjectSessions.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                items(subjectSessions, key = { it.session.id }) { sessionItem ->
                    ModernSessionCard(
                        sessionItem = sessionItem,
                        onClick = { onNavigateToSessionDetail(sessionItem.session.id) }
                    )
                }
            }
        }
    }
}
