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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: EduTrackViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val userName by viewModel.userName.collectAsState()
    val studentId by viewModel.studentId.collectAsState()
    val primaryMajor by viewModel.primaryMajor.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val currentSemester by viewModel.currentSemester.collectAsState()
    val targetWeeklyHours by viewModel.targetWeeklyHours.collectAsState()
    val stats by viewModel.dashboardStats.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val sessionItems by viewModel.sessionsWithSubject.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userName) }
    var editEmail by remember { mutableStateOf(userEmail) }
    var editStudentId by remember { mutableStateOf(studentId) }
    var editMajor by remember { mutableStateOf(primaryMajor) }
    var editSemester by remember { mutableStateOf(currentSemester) }
    var editWeeklyHours by remember { mutableStateOf(String.format(Locale.US, "%.0f", targetWeeklyHours)) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var idError by remember { mutableStateOf<String?>(null) }

    val userInitials = remember(userName) {
        userName.trim().split("\\s+".toRegex())
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("").take(2).ifEmpty { "TG" }
    }

    val totalHoursLogged = remember(sessionItems) {
        val totalMins = sessionItems.sumOf { it.session.durationMinutes }
        String.format(Locale.US, "%.1fh", totalMins / 60.0)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkAuroraBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Student Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (!isEditing) {
                        FilledTonalButton(
                            onClick = {
                                editName = userName
                                editEmail = userEmail
                                editStudentId = studentId
                                editMajor = primaryMajor
                                editSemester = currentSemester
                                editWeeklyHours = String.format(Locale.US, "%.0f", targetWeeklyHours)
                                isEditing = true
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ElectricIndigo.copy(alpha = 0.25f),
                                contentColor = ElectricIndigo
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    } else {
                        TextButton(
                            onClick = { isEditing = false }
                        ) {
                            Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkAuroraBg
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Hero Profile Avatar Banner
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricIndigo, RadiantViolet, NeonCyan)
                        )
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(MidnightSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitials,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    color = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElectricIndigo.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "ID: $studentId",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricIndigo
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonCyan.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = primaryMajor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                    }
                }
            }

            // 2. Academic Stats Overview Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    title = "STUDY STREAK",
                    value = "${stats.activeStreakDays} Days",
                    icon = Icons.Default.LocalFireDepartment,
                    tint = StreakOrange,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    title = "XP EARNED",
                    value = "${stats.totalXp}",
                    icon = Icons.Default.EmojiEvents,
                    tint = AmberGold,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    title = "ALL-TIME",
                    value = totalHoursLogged,
                    icon = Icons.Default.Timer,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Edit Form OR Detailed Info Cards
            if (isEditing) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ElectricIndigo.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "Edit Student Information",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        // Full Name
                        OutlinedTextField(
                            value = editName,
                            onValueChange = {
                                editName = it
                                nameError = if (it.isBlank()) "Full name cannot be empty" else null
                            },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ElectricIndigo) },
                            isError = nameError != null,
                            supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Account Email
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Account Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = RadiantViolet) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Student ID
                        OutlinedTextField(
                            value = editStudentId,
                            onValueChange = {
                                editStudentId = it
                                idError = if (it.isBlank()) "Student ID is required" else null
                            },
                            label = { Text("Student ID (e.g. I26043415)") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = NeonCyan) },
                            isError = idError != null,
                            supportingText = { idError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Degree / Major
                        OutlinedTextField(
                            value = editMajor,
                            onValueChange = { editMajor = it },
                            label = { Text("Degree / Programme (e.g. BCSI / BITI)") },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = RadiantViolet) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Academic Semester Selector
                        Text(
                            text = "Current Academic Semester:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3, 4, 5, 6).forEach { sem ->
                                val isSelected = editSemester == sem
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { editSemester = sem }
                                        .border(
                                            1.dp,
                                            if (isSelected) ElectricIndigo else MidnightBorder,
                                            RoundedCornerShape(10.dp)
                                        ),
                                    color = if (isSelected) ElectricIndigo.copy(alpha = 0.25f) else Color.Transparent
                                ) {
                                    Text(
                                        text = "S$sem",
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        textAlign = TextAlign.Center,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Weekly Target Hours
                        OutlinedTextField(
                            value = editWeeklyHours,
                            onValueChange = { editWeeklyHours = it },
                            label = { Text("Target Weekly Study Hours (e.g. 26)") },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = StreakOrange) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                if (editName.isBlank()) {
                                    nameError = "Full name cannot be empty"
                                    return@Button
                                }
                                if (editStudentId.isBlank()) {
                                    idError = "Student ID is required"
                                    return@Button
                                }
                                val hours = editWeeklyHours.toDoubleOrNull() ?: targetWeeklyHours
                                viewModel.updateUserProfile(
                                    name = editName,
                                    email = editEmail,
                                    id = editStudentId,
                                    major = editMajor,
                                    weeklyGoalHours = hours,
                                    semester = editSemester
                                )
                                isEditing = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Profile Changes", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            } else {
                // View Mode: Academic Record Cards
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MidnightBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "Academic Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Account Email",
                            value = userEmail.ifBlank { "student@university.edu" },
                            tint = RadiantViolet
                        )

                        HorizontalDivider(color = MidnightBorder.copy(alpha = 0.5f))

                        ProfileInfoRow(
                            icon = Icons.Default.Badge,
                            label = "Student Matric ID",
                            value = studentId,
                            tint = ElectricIndigo
                        )

                        HorizontalDivider(color = MidnightBorder.copy(alpha = 0.5f))

                        ProfileInfoRow(
                            icon = Icons.Default.School,
                            label = "Programme / Major",
                            value = primaryMajor,
                            tint = NeonCyan
                        )

                        HorizontalDivider(color = MidnightBorder.copy(alpha = 0.5f))

                        ProfileInfoRow(
                            icon = Icons.Default.CalendarMonth,
                            label = "Academic Standing",
                            value = "Semester $currentSemester",
                            tint = AmberGold
                        )

                        HorizontalDivider(color = MidnightBorder.copy(alpha = 0.5f))

                        ProfileInfoRow(
                            icon = Icons.Default.TrackChanges,
                            label = "Target Weekly Pace",
                            value = String.format(Locale.US, "%.1f hours / week (%.1fh / day)", targetWeeklyHours, targetWeeklyHours / 7.0),
                            tint = AmberGold
                        )

                        HorizontalDivider(color = MidnightBorder.copy(alpha = 0.5f))

                        ProfileInfoRow(
                            icon = Icons.Default.Book,
                            label = "Enrolled Modules",
                            value = "${subjects.size} Active Subjects",
                            tint = EmeraldGreen
                        )
                    }
                }

                // Focus Highlights Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MidnightBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AmberGold)
                            Text(
                                "Study Highlights",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Text(
                            text = "• Most focused subject: ${stats.mostStudiedSubjectName} (${stats.mostStudiedSubjectHoursText})\n" +
                                   "• Most productive day of week: ${stats.bestStudyDayName} (${stats.bestStudyDayDurationText})\n" +
                                   "• Total logged study sessions: ${sessionItems.size} sessions",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Log Out Button
            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = RadicalCoral
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, RadicalCoral.copy(alpha = 0.5f))
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = RadicalCoral)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out of Student Account", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, MidnightBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color.White
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
            )
        }
    }
}
