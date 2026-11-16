package com.edutrack.gti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel
import com.edutrack.gti.util.AmbientSoundPlayer
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTimerScreen(
    viewModel: EduTrackViewModel,
    initialSubjectId: Int = -1,
    initialTopicName: String = "",
    onNavigateBack: () -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    var selectedSubjectId by remember {
        mutableIntStateOf(if (initialSubjectId > 0) initialSubjectId else subjects.firstOrNull()?.id ?: -1)
    }
    var topicName by remember {
        mutableStateOf(if (initialTopicName.isNotBlank()) initialTopicName else "Core Practice")
    }
    var notesText by remember { mutableStateOf("") }

    LaunchedEffect(subjects) {
        if (selectedSubjectId <= 0 && subjects.isNotEmpty()) {
            selectedSubjectId = subjects.first().id
            if (topicName == "Core Practice") {
                topicName = subjects.first().nextTopic
            }
        }
    }

    var isRunning by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    var showCompletionModal by remember { mutableStateOf(false) }

    var timerMode by remember { mutableIntStateOf(0) } // 0 = Stopwatch, 1 = Pomodoro
    var pomodoroMinutes by remember { mutableIntStateOf(25) }
    var activeAmbientSound by remember { mutableStateOf("Mute") }
    var pomodorosCompleted by remember { mutableIntStateOf(0) }

    val targetSeconds = pomodoroMinutes * 60

    // Lifecycle cleanup for Ambient Audio Synthesizer
    DisposableEffect(Unit) {
        onDispose {
            AmbientSoundPlayer.stopSound()
        }
    }

    DisposableEffect(activeAmbientSound) {
        AmbientSoundPlayer.playSound(activeAmbientSound)
        onDispose { }
    }

    LaunchedEffect(isRunning, timerMode, targetSeconds) {
        while (isRunning) {
            delay(1000L)
            secondsElapsed++
            if (timerMode == 1 && secondsElapsed >= targetSeconds) {
                isRunning = false
                pomodorosCompleted++
                showCompletionModal = true
            }
        }
    }

    val displaySeconds = if (timerMode == 1) (targetSeconds - secondsElapsed).coerceAtLeast(0) else secondsElapsed
    val minutes = displaySeconds / 60
    val hours = minutes / 60
    val formattedTime = if (timerMode == 1) {
        String.format("%02d:%02d", minutes % 60, displaySeconds % 60)
    } else {
        String.format("%02d:%02d:%02d", hours, minutes % 60, displaySeconds % 60)
    }

    val selectedSubject = subjects.find { it.id == selectedSubjectId }
    val color = parseHexColor(selectedSubject?.colorHex ?: "#6366F1")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Study Timer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (secondsElapsed > 0) {
                        IconButton(
                            onClick = {
                                isRunning = false
                                secondsElapsed = 0
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Timer")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkAuroraBg)
            )
        },
        containerColor = DarkAuroraBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Segmented Timer Mode Control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MidnightSurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val modes = listOf("⏱ Stopwatch", "🍅 Pomodoro (${pomodoroMinutes}m)")
                    modes.forEachIndexed { idx, label ->
                        val isSel = timerMode == idx
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    timerMode = idx
                                    isRunning = false
                                    secondsElapsed = 0
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) ElectricIndigo else Color.Transparent
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Customizable Pomodoro Time Selector Chips (shown when Pomodoro mode is active)
                if (timerMode == 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DURATIONS:",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(15, 25, 45, 60).forEach { dur ->
                                val isSelectedDur = pomodoroMinutes == dur
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelectedDur) ElectricIndigo else MidnightSurface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelectedDur) NeonCyan else MidnightBorder
                                    ),
                                    modifier = Modifier.clickable {
                                        pomodoroMinutes = dur
                                        secondsElapsed = 0
                                        isRunning = false
                                    }
                                ) {
                                    Text(
                                        text = "${dur}m",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MidnightSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder),
                                modifier = Modifier.clickable {
                                    if (pomodoroMinutes > 5) {
                                        pomodoroMinutes -= 5
                                        secondsElapsed = 0
                                        isRunning = false
                                    }
                                }
                            ) {
                                Text(
                                    text = "-5",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = StreakOrange
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MidnightSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder),
                                modifier = Modifier.clickable {
                                    if (pomodoroMinutes < 180) {
                                        pomodoroMinutes += 5
                                        secondsElapsed = 0
                                        isRunning = false
                                    }
                                }
                            ) {
                                Text(
                                    text = "+5",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldGreen
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SELECT SUBJECT",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects, key = { it.id }) { subject ->
                        val isSelected = subject.id == selectedSubjectId
                        val subColor = parseHexColor(subject.colorHex)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) subColor.copy(alpha = 0.25f) else MidnightSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) subColor else MidnightBorder
                            ),
                            modifier = Modifier.clickable {
                                selectedSubjectId = subject.id
                                topicName = subject.nextTopic
                            }
                        ) {
                            Text(
                                text = subject.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) subColor else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ambient Focus Sound Selector
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎧 Ambient Sound: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val sounds = listOf("Mute", "Rain", "Forest", "White Noise", "Cafe")
                        val soundIcons = mapOf(
                            "Mute" to "🔇 Mute",
                            "Rain" to "🌧️ Rain",
                            "Forest" to "🌲 Forest",
                            "White Noise" to "🎧 White Noise",
                            "Cafe" to "☕ Cafe"
                        )
                        items(sounds) { sndKey ->
                            val isSel = sndKey == activeAmbientSound
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) ElectricIndigo.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ElectricIndigo else Color.Transparent),
                                modifier = Modifier.clickable {
                                    activeAmbientSound = sndKey
                                }
                            ) {
                                Text(
                                    text = soundIcons[sndKey] ?: sndKey,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = if (isSel) ElectricIndigo else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(MidnightSurface)
                    .border(
                        width = 4.dp,
                        brush = Brush.sweepGradient(
                            listOf(color, ElectricIndigo, NeonCyan, color)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isRunning) (if (timerMode == 1) "FOCUSING (POMODORO)" else "STUDYING NOW") else if (secondsElapsed > 0) "PAUSED" else "READY",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isRunning) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 44.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (timerMode == 1) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StreakOrange.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🍅 $pomodorosCompleted Completed",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = StreakOrange
                            )
                        }
                    } else {
                        Text(
                            text = selectedSubject?.name ?: "Select a Subject",
                            style = MaterialTheme.typography.bodySmall,
                            color = color
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
                    Button(
                        onClick = {
                            try { haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress) } catch (e: Exception) {}
                            isRunning = !isRunning
                        },
                        enabled = subjects.isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRunning) StreakOrange else ElectricIndigo
                        )
                    ) {
                        Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when {
                                isRunning -> "Pause"
                                secondsElapsed > 0 -> "Resume"
                                else -> "Start Session"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (secondsElapsed > 0) {
                        Button(
                            onClick = {
                                try { haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress) } catch (e: Exception) {}
                                isRunning = false
                                showCompletionModal = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Complete", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        if (showCompletionModal) {
            val elapsedMinutes = (secondsElapsed / 60).coerceAtLeast(1)
            val sessionCompletedMinutes = if (timerMode == 1 && secondsElapsed >= targetSeconds) pomodoroMinutes else elapsedMinutes
            val xpReward = remember(sessionCompletedMinutes) {
                (sessionCompletedMinutes * 0.8).toInt() + 20
            }

            AlertDialog(
                onDismissRequest = { showCompletionModal = false },
                containerColor = MidnightSurface,
                title = { Text("Session Complete! 🎉", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Awesome work! You completed $sessionCompletedMinutes minute${if (sessionCompletedMinutes == 1) "" else "s"} of study for ${selectedSubject?.name ?: "Subject"}.")
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RadiantViolet.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🏆 +$xpReward XP Earned!  |  🔥 Streak Updated",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = RadiantViolet
                            )
                        }
                        OutlinedTextField(
                            value = topicName,
                            onValueChange = { topicName = it },
                            label = { Text("Topic Studied") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            placeholder = { Text("Add study reflection notes...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCompletionModal = false
                            viewModel.logCompletedTimerSession(
                                subjectId = selectedSubjectId,
                                durationMinutes = sessionCompletedMinutes,
                                topicName = topicName,
                                notes = notesText,
                                onSuccess = onNavigateBack
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text("Save & Claim XP", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCompletionModal = false }) {
                        Text("Discard")
                    }
                }
            )
        }
    }
}
