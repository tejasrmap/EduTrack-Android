package com.edutrack.gti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel

@Composable
fun OnboardingWelcomeScreen(
    viewModel: EduTrackViewModel,
    onOnboardingComplete: () -> Unit
) {
    val initialName by viewModel.userName.collectAsState()
    val initialId by viewModel.studentId.collectAsState()
    val initialHours by viewModel.targetWeeklyHours.collectAsState()
    val initialMajor by viewModel.primaryMajor.collectAsState()

    var nameInput by remember { mutableStateOf(initialName) }
    var idInput by remember { mutableStateOf(initialId) }
    var majorInput by remember { mutableStateOf(initialMajor) }
    var weeklyHoursInput by remember { mutableStateOf(initialHours.toFloat()) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var idError by remember { mutableStateOf<String?>(null) }

    val calculatedDailyPace = remember(weeklyHoursInput) {
        Math.round((weeklyHoursInput / 7.0f) * 10f) / 10f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkAuroraBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App Icon Logo Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(HeroAuroraGradient)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(MidnightSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎓",
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Titles
            Text(
                text = "Welcome to EduTrack 2.0",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your Intelligent Study & Exam Prep Companion",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Form Glass Card Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LiquidGlassBorderColor,
                        shape = RoundedCornerShape(26.dp)
                    ),
                shape = RoundedCornerShape(26.dp),
                color = MidnightSurface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "STUDENT ACADEMIC PROFILE",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                        fontWeight = FontWeight.Bold,
                        color = ElectricIndigo
                    )

                    // 1. Full Name Input
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { str ->
                            nameInput = str
                            if (str.isNotBlank()) nameError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. Teja G") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ElectricIndigo) },
                        isError = nameError != null,
                        supportingText = if (nameError != null) { { Text(nameError!!, color = MaterialTheme.colorScheme.error) } } else null,
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    // 2. Student ID Input
                    OutlinedTextField(
                        value = idInput,
                        onValueChange = { str ->
                            idInput = str
                            if (str.isNotBlank()) idError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Student ID / Matriculation No") },
                        placeholder = { Text("e.g. I26043415") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = NeonCyan) },
                        isError = idError != null,
                        supportingText = if (idError != null) { { Text(idError!!, color = MaterialTheme.colorScheme.error) } } else null,
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    // 3. Academic Program / Major Input
                    OutlinedTextField(
                        value = majorInput,
                        onValueChange = { majorInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Academic Program / Major") },
                        placeholder = { Text("e.g. Mobile Apps & Software Eng") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = RadiantViolet) },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    // 4. Target Weekly Study Goal Selector
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Weekly Goal Target",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ElectricIndigo.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${weeklyHoursInput.toInt()} hrs / week",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = ElectricIndigo
                                )
                            }
                        }

                        // Preset Target Hours Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(15f, 26f, 35f, 45f).forEach { hours ->
                                val isSelected = weeklyHoursInput.toInt() == hours.toInt()
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) ElectricIndigo else MidnightSurfaceVariant)
                                        .clickable { weeklyHoursInput = hours }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${hours.toInt()}h",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Slider(
                            value = weeklyHoursInput,
                            onValueChange = { weeklyHoursInput = it },
                            valueRange = 10f..60f,
                            steps = 50,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricIndigo,
                                activeTrackColor = ElectricIndigo,
                                inactiveTrackColor = MidnightBorder
                            )
                        )

                        // Informational Calculated Pace Badge
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldGreen.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Daily Target Commitment: ${calculatedDailyPace} hrs/day across registered subjects",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = EmeraldGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            var isValid = true
                            if (nameInput.isBlank()) {
                                nameError = "Name cannot be empty"
                                isValid = false
                            }
                            if (idInput.isBlank()) {
                                idError = "Student ID cannot be empty"
                                isValid = false
                            }

                            if (isValid) {
                                viewModel.completeOnboarding(
                                    name = nameInput,
                                    id = idInput,
                                    weeklyHours = weeklyHoursInput.toDouble(),
                                    major = majorInput
                                )
                                onOnboardingComplete()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "✨ Launch EduTrack Dashboard",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
