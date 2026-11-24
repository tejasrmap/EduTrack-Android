package com.edutrack.gti.ui.screens

import android.util.Patterns
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: EduTrackViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var currentStep by remember { mutableStateOf(1) } // 1: Account, 2: Academic, 3: Goals

    // Step 1: Account Credentials
    var name by remember { mutableStateOf("Teja G") }
    var email by remember { mutableStateOf("teja.g@student.edu") }
    var password by remember { mutableStateOf("Student123!") }
    var confirmPassword by remember { mutableStateOf("Student123!") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    // Step 2: Academic Details
    var studentId by remember { mutableStateOf("I26043415") }
    var major by remember { mutableStateOf("Mobile Apps & Software Eng") }
    var selectedSemester by remember { mutableStateOf(3) }
    var studentIdError by remember { mutableStateOf<String?>(null) }

    // Step 3: Study Routine & Goals
    var weeklyHoursInput by remember { mutableStateOf(26f) }
    var primaryGoal by remember { mutableStateOf("Exam Preparation & High GPA") }

    val calculatedDailyPace = remember(weeklyHoursInput) {
        Math.round((weeklyHoursInput / 7.0f) * 10f) / 10f
    }

    val majorPresets = listOf(
        "Mobile Apps & Software Eng",
        "Computer Science",
        "Information Technology",
        "Data Science & AI",
        "Software Engineering"
    )

    val goalPresets = listOf(
        "Exam Preparation & High GPA",
        "Daily Consistency & Habits",
        "Deep Skill Mastery & Practical Coding",
        "Coursework Tracking & Deadlines"
    )

    fun validateStep1(): Boolean {
        var valid = true
        nameError = null
        emailError = null
        passwordError = null
        confirmError = null

        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isEmpty()) {
            nameError = "Full name cannot be empty"
            valid = false
        } else if (trimmedName.length < 2) {
            nameError = "Name must be at least 2 characters"
            valid = false
        }

        if (trimmedEmail.isEmpty()) {
            emailError = "Student email cannot be empty"
            valid = false
        } else if (!trimmedEmail.contains("@")) {
            emailError = "Please enter a valid email address"
            valid = false
        }

        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            valid = false
        }

        if (confirmPassword != password) {
            confirmError = "Passwords do not match"
            valid = false
        }

        return valid
    }

    fun validateStep2(): Boolean {
        var valid = true
        studentIdError = null

        if (studentId.trim().isEmpty()) {
            studentIdError = "Student Matric ID is required"
            valid = false
        }
        return valid
    }

    fun handleCompleteSignUp() {
        focusManager.clearFocus()
        viewModel.register(
            name = name,
            email = email,
            pass = password,
            studentId = studentId,
            major = major,
            weeklyHours = weeklyHoursInput.toDouble(),
            semester = selectedSemester
        )
        onSignUpSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkAuroraBg)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // App Brand Logo Crest
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(HeroAuroraGradient)
                    .padding(2.5.dp)
                    .clip(CircleShape)
                    .background(MidnightSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎓",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Guided Student Onboarding",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when (currentStep) {
                    1 -> "Step 1 of 3: Set up your account credentials"
                    2 -> "Step 2 of 3: Academic programme & matric details"
                    else -> "Step 3 of 3: Weekly study commitment & goals"
                },
                style = MaterialTheme.typography.bodySmall,
                color = NeonCyan,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Step Indicator Wizard Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Step 1
                StepNode(
                    stepNumber = 1,
                    title = "Account",
                    isActive = currentStep == 1,
                    isCompleted = currentStep > 1
                )

                // Connector 1-2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (currentStep > 1) EmeraldGreen else MidnightBorder)
                )

                // Step 2
                StepNode(
                    stepNumber = 2,
                    title = "Academics",
                    isActive = currentStep == 2,
                    isCompleted = currentStep > 2
                )

                // Connector 2-3
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (currentStep > 2) EmeraldGreen else MidnightBorder)
                )

                // Step 3
                StepNode(
                    stepNumber = 3,
                    title = "Goals",
                    isActive = currentStep == 3,
                    isCompleted = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LiquidGlassBorderColor, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // =================================================================
                    // STEP 1: ACCOUNT CREDENTIALS
                    // =================================================================
                    if (currentStep == 1) {
                        Text(
                            text = "1. ACCOUNT & SECURITY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = ElectricIndigo
                        )

                        // Full Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                if (nameError != null) nameError = null
                            },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Teja G") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ElectricIndigo) },
                            isError = nameError != null,
                            supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Email Address
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                if (emailError != null) emailError = null
                            },
                            label = { Text("Student Email Address") },
                            placeholder = { Text("e.g. teja.g@student.edu") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
                            isError = emailError != null,
                            supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                if (passwordError != null) passwordError = null
                            },
                            label = { Text("Password (min 6 chars)") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = RadiantViolet) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = passwordError != null,
                            supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                if (confirmError != null) confirmError = null
                            },
                            label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = AmberGold) },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = confirmError != null,
                            supportingText = { confirmError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                if (validateStep1()) currentStep = 2
                            }),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // =================================================================
                    // STEP 2: ACADEMIC DETAILS
                    // =================================================================
                    if (currentStep == 2) {
                        Text(
                            text = "2. ACADEMIC PROGRAMME & MATRIC",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = NeonCyan
                        )

                        // Student Matric ID
                        OutlinedTextField(
                            value = studentId,
                            onValueChange = {
                                studentId = it
                                if (studentIdError != null) studentIdError = null
                            },
                            label = { Text("Student Matriculation ID *") },
                            placeholder = { Text("e.g. I26043415") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = ElectricIndigo) },
                            isError = studentIdError != null,
                            supportingText = { studentIdError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Major / Degree Programme
                        OutlinedTextField(
                            value = major,
                            onValueChange = { major = it },
                            label = { Text("Academic Major / Degree") },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = EmeraldGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                if (validateStep2()) currentStep = 3
                            }),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Select or tap programme suggestion:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        // Preset Major Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            majorPresets.forEach { preset ->
                                val isSelected = major.equals(preset, ignoreCase = true)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { major = preset },
                                    label = { Text(preset, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricIndigo.copy(alpha = 0.35f),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Current Semester Selector
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
                                val isSelected = selectedSemester == sem
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedSemester = sem }
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
                    }

                    // =================================================================
                    // STEP 3: STUDY GOALS & REVIEW
                    // =================================================================
                    if (currentStep == 3) {
                        Text(
                            text = "3. WEEKLY COMMITMENT & GOALS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = AmberGold
                        )

                        // Target Hours Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Weekly Study Target",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ElectricIndigo.copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricIndigo)
                            ) {
                                Text(
                                    text = "${weeklyHoursInput.toInt()} hrs / week",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.White
                                )
                            }
                        }

                        // Target Hours Quick Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15f, 20f, 26f, 35f, 45f).forEach { hours ->
                                val isSelected = weeklyHoursInput == hours
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { weeklyHoursInput = hours }
                                        .border(
                                            1.dp,
                                            if (isSelected) ElectricIndigo else MidnightBorder,
                                            RoundedCornerShape(8.dp)
                                        ),
                                    color = if (isSelected) ElectricIndigo.copy(alpha = 0.3f) else Color.Transparent
                                ) {
                                    Text(
                                        text = "${hours.toInt()}h",
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Slider
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

                        // Calculated Daily Commitment Badge
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
                                    text = "Daily Target Commitment: ${calculatedDailyPace} hrs/day across enrolled modules",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = EmeraldGreen
                                )
                            }
                        }

                        // Primary Motivation Chips
                        Text(
                            text = "Primary Academic Focus:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            goalPresets.forEach { goal ->
                                val isSelected = primaryGoal == goal
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { primaryGoal = goal }
                                        .border(
                                            1.dp,
                                            if (isSelected) AmberGold else MidnightBorder,
                                            RoundedCornerShape(10.dp)
                                        ),
                                    color = if (isSelected) AmberGold.copy(alpha = 0.15f) else Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isSelected) AmberGold else Color.White.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = goal,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Summary Confirmation Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = DarkAuroraBg.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Student Registration Summary", style = MaterialTheme.typography.labelMedium, color = ElectricIndigo, fontWeight = FontWeight.Bold)
                                Text("• Student: $name ($email)", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                                Text("• Matric ID: $studentId | Semester $selectedSemester", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                                Text("• Programme: $major", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                                Text("• Commitment: ${weeklyHoursInput.toInt()}h / week ($calculatedDailyPace hrs/day)", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step Navigation Action Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStep > 1) {
                            OutlinedButton(
                                onClick = { currentStep -= 1 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Back", fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                when (currentStep) {
                                    1 -> {
                                        if (validateStep1()) {
                                            focusManager.clearFocus()
                                            currentStep = 2
                                        }
                                    }
                                    2 -> {
                                        if (validateStep2()) {
                                            focusManager.clearFocus()
                                            currentStep = 3
                                        }
                                    }
                                    3 -> {
                                        handleCompleteSignUp()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(if (currentStep > 1) 2f else 1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentStep == 3) EmeraldGreen else ElectricIndigo
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = when (currentStep) {
                                        1 -> "Next: Academics →"
                                        2 -> "Next: Study Goals →"
                                        else -> "🎉 Launch EduTrack"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer link to Login
            Row(
                modifier = Modifier
                    .padding(bottom = 36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigateToLogin() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "Sign In Here",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricIndigo
                    )
                )
            }
        }
    }
}

@Composable
private fun StepNode(
    stepNumber: Int,
    title: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> EmeraldGreen
                        isActive -> ElectricIndigo
                        else -> MidnightSurface
                    }
                )
                .border(
                    1.5.dp,
                    when {
                        isCompleted -> EmeraldGreen
                        isActive -> ElectricIndigo
                        else -> MidnightBorder
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = "$stepNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isActive || isCompleted) Color.White else Color.White.copy(alpha = 0.5f)
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) Color.White else Color.White.copy(alpha = 0.6f)
        )
    }
}
