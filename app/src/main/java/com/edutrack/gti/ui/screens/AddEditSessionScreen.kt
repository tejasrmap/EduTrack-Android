package com.edutrack.gti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSessionScreen(
    viewModel: EduTrackViewModel,
    sessionId: Int,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.sessionFormState.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    val isEditMode = sessionId > 0
    var subjectDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val selectedSubject = subjects.find { it.id == formState.selectedSubjectId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Study Session" else "Log Study Session",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBackground)
            )
        },
        containerColor = MidnightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Card 1: Subject Selection (Material 3 ExposedDropdownMenuBox + DropdownMenu) & Topic
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MidnightBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SELECT SUBJECT *",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (subjects.isEmpty()) {
                        Text(
                            text = "No subjects available. Please add a subject in Subject Manager first.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = subjectDropdownExpanded,
                            onExpandedChange = { subjectDropdownExpanded = !subjectDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedSubject?.name ?: "Choose a subject...",
                                onValueChange = {},
                                readOnly = true,
                                isError = formState.subjectError != null,
                                leadingIcon = selectedSubject?.let { sub ->
                                    {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(parseHexColor(sub.colorHex))
                                        )
                                    }
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = subjectDropdownExpanded,
                                onDismissRequest = { subjectDropdownExpanded = false },
                                modifier = Modifier.background(MidnightSurfaceVariant)
                            ) {
                                subjects.forEach { subject ->
                                    val subColor = parseHexColor(subject.colorHex)
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(subColor)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = subject.name,
                                                    fontWeight = if (subject.id == formState.selectedSubjectId) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.updateSessionSubject(subject.id)
                                            subjectDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (formState.subjectError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = formState.subjectError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "TOPIC STUDIED",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formState.topicText,
                        onValueChange = { viewModel.updateSessionTopic(it) },
                        placeholder = { Text("e.g. Room Entities & DAO Queries") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            // Card 2: Duration Input & Quick Presets
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MidnightBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DURATION (MINUTES) *",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = formState.durationText,
                        onValueChange = { viewModel.updateSessionDuration(it) },
                        isError = formState.durationError != null,
                        placeholder = { Text("e.g. 60") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                    if (formState.durationError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formState.durationError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Quick Select Duration:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf(30, 45, 60, 90, 120)
                        presets.forEach { preset ->
                            val isSelected = formState.durationText == preset.toString()
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateSessionDuration(preset.toString()) },
                                label = { Text("${preset}m", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricIndigo,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Card 3: Date & Quick Chips
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MidnightBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DATE (YYYY-MM-DD) *",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = formState.dateText,
                        onValueChange = { viewModel.updateSessionDate(it) },
                        isError = formState.dateError != null,
                        placeholder = { Text("YYYY-MM-DD") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePickerDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Pick Date",
                                    tint = ElectricIndigo
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                    if (formState.dateError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formState.dateError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = { viewModel.updateSessionDate(LocalDate.now().toString()) },
                            label = { Text("Today") },
                            shape = RoundedCornerShape(10.dp)
                        )
                        AssistChip(
                            onClick = { viewModel.updateSessionDate(LocalDate.now().minusDays(1).toString()) },
                            label = { Text("Yesterday") },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Card 4: Focus Rating & Notes
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MidnightBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "FOCUS RATING",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { viewModel.updateSessionRating(star) }) {
                                Icon(
                                    imageVector = if (star <= formState.rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Star $star",
                                    tint = if (star <= formState.rating) GlowingAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        val ratingText = when (formState.rating) {
                            5 -> "⚡ 5/5 Ultra Focus"
                            4 -> "🌟 4/5 Good Focus"
                            3 -> "👍 3/5 Moderate"
                            2 -> "⚠️ 2/5 Distracted"
                            else -> "😴 1/5 Low Focus"
                        }
                        Text(
                            text = ratingText,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = GlowingAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "STUDY NOTES",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = formState.notesText,
                        onValueChange = { viewModel.updateSessionNotes(it) },
                        placeholder = { Text("Record key concepts studied, problems solved, or exam prep notes...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CTA Save Button
            Button(
                onClick = {
                    viewModel.validateAndSaveSession(onSuccess = onNavigateBack)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditMode) "Save Changes" else "Log Study Session",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        if (showDatePickerDialog) {
            val initialEpochMillis = remember(formState.dateText) {
                try {
                    LocalDate.parse(formState.dateText.trim())
                        .atStartOfDay(ZoneId.of("UTC"))
                        .toInstant()
                        .toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            }
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialEpochMillis)

            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val pickedDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.of("UTC"))
                                    .toLocalDate()
                                viewModel.updateSessionDate(pickedDate.toString())
                            }
                            showDatePickerDialog = false
                        }
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold, color = ElectricIndigo)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerDialog = false }) {
                        Text("Cancel")
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = MidnightSurface
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = MidnightSurface,
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        yearContentColor = Color.White,
                        currentYearContentColor = NeonCyan,
                        selectedYearContentColor = Color.White,
                        selectedYearContainerColor = ElectricIndigo,
                        dayContentColor = Color.White,
                        selectedDayContentColor = Color.White,
                        selectedDayContainerColor = ElectricIndigo,
                        todayContentColor = NeonCyan,
                        todayDateBorderColor = NeonCyan
                    )
                )
            }
        }
    }
}
