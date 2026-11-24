package com.edutrack.gti

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.edutrack.gti.ui.navigation.Screen
import com.edutrack.gti.ui.screens.*
import com.edutrack.gti.ui.theme.*
import com.edutrack.gti.ui.viewmodel.EduTrackViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: EduTrackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduTrackTheme(darkTheme = true) {
                EduTrackMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EduTrackMainApp(viewModel: EduTrackViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.History,
        Screen.Stats,
        Screen.Journey,
        Screen.Subjects
    )

    val isBottomBarVisible = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkAuroraBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Navigation Graph
            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToSignUp = {
                            navController.navigate(Screen.SignUp.route)
                        }
                    )
                }

                composable(Screen.SignUp.route) {
                    SignUpScreen(
                        viewModel = viewModel,
                        onSignUpSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.WelcomeOnboarding.route) {
                    OnboardingWelcomeScreen(
                        viewModel = viewModel,
                        onOnboardingComplete = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.WelcomeOnboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onContinueStudying = { subject, topic ->
                            val subId = subject?.id ?: -1
                            val encodedTopic = Uri.encode(topic)
                            navController.navigate("live_timer?subjectId=$subId&topicName=$encodedTopic")
                        },
                        onNavigateToSubjectManager = {
                            navController.navigate(Screen.Subjects.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToSessionDetail = { sessionId ->
                            navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }

                composable(Screen.History.route) {
                    SessionHistoryScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { sessionId ->
                            navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                        }
                    )
                }

                composable(Screen.Stats.route) {
                    ProgressStatsScreen(viewModel = viewModel)
                }

                composable(Screen.Journey.route) {
                    StudyJourneyScreen(
                        viewModel = viewModel,
                        onSubjectClick = { subId -> navController.navigate("subject_detail/$subId") }
                    )
                }

                composable(Screen.Subjects.route) {
                    SubjectManagerScreen(viewModel = viewModel)
                }

                composable(
                    route = "live_timer?subjectId={subjectId}&topicName={topicName}",
                    arguments = listOf(
                        navArgument("subjectId") { type = NavType.IntType; defaultValue = -1 },
                        navArgument("topicName") { type = NavType.StringType; defaultValue = "Core Practice" }
                    )
                ) { backStackEntry ->
                    val subId = backStackEntry.arguments?.getInt("subjectId") ?: -1
                    val topic = backStackEntry.arguments?.getString("topicName") ?: "Core Practice"
                    LiveTimerScreen(
                        viewModel = viewModel,
                        initialSubjectId = subId,
                        initialTopicName = topic,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = "subject_detail/{subjectId}",
                    arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val subId = backStackEntry.arguments?.getInt("subjectId") ?: -1
                    SubjectDetailScreen(
                        viewModel = viewModel,
                        subjectId = subId,
                        onStartTimer = { sId, topic ->
                            val encodedTopic = Uri.encode(topic)
                            navController.navigate("live_timer?subjectId=$sId&topicName=$encodedTopic")
                        },
                        onNavigateToSessionDetail = { sessionId ->
                            navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.AddEditSession.route,
                    arguments = listOf(navArgument("sessionId") { type = NavType.IntType; defaultValue = -1 })
                ) { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: -1
                    AddEditSessionScreen(
                        viewModel = viewModel,
                        sessionId = sessionId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.SessionDetail.route,
                    arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: -1
                    SessionDetailScreen(
                        viewModel = viewModel,
                        sessionId = sessionId,
                        onNavigateToEdit = { id -> navController.navigate(Screen.AddEditSession.createRoute(id)) },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Profile.route) {
                    UserProfileScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // Floating Quick-Action Orb: ONLY visible on Dashboard (HomeScreen) to keep other screens spacious and uncongested
            if (currentRoute == Screen.Home.route) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(bottom = 84.dp, end = 16.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AnimatedVisibility(
                        visible = isFabMenuExpanded,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .border(1.dp, LiquidGlassBorderColor, RoundedCornerShape(14.dp))
                                    .clickable {
                                        isFabMenuExpanded = false
                                        val encoded = Uri.encode("Core Practice")
                                        navController.navigate("live_timer?subjectId=-1&topicName=$encoded")
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = MidnightSurface,
                                tonalElevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = ElectricIndigo, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("⏱ Start Session Timer", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .border(1.dp, LiquidGlassBorderColor, RoundedCornerShape(14.dp))
                                    .clickable {
                                        isFabMenuExpanded = false
                                        viewModel.prepareNewSessionForm()
                                        navController.navigate(Screen.AddEditSession.createRoute(-1))
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = MidnightSurface,
                                tonalElevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("📝 Manual Log Session", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = { isFabMenuExpanded = !isFabMenuExpanded },
                        containerColor = ElectricIndigo,
                        shape = CircleShape,
                        modifier = Modifier.size(52.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (isFabMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Action Menu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Full Width Floating Navigation Dock
            if (isBottomBarVisible) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = LiquidGlassBorderColor,
                                shape = RoundedCornerShape(26.dp)
                            ),
                        shape = RoundedCornerShape(26.dp),
                        color = MidnightSurface.copy(alpha = 0.96f),
                        tonalElevation = 10.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            bottomNavItems.forEach { screen ->
                                val isSelected = currentRoute == screen.route
                                val iconVector = screen.icon ?: Icons.Default.Home
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) ElectricIndigo.copy(alpha = 0.22f) else Color.Transparent)
                                        .clickable {
                                            isFabMenuExpanded = false
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                        .padding(vertical = 6.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) ElectricIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = screen.title,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
