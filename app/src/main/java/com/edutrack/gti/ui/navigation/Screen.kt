package com.edutrack.gti.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Dashboard", Icons.Default.Home)
    object History : Screen("history", "History", Icons.Default.History)
    object Stats : Screen("stats", "Stats", Icons.Default.BarChart)
    object Journey : Screen("journey", "Journey", Icons.Default.School)
    object Subjects : Screen("subjects", "Subjects", Icons.Default.Book)
    
    object AddEditSession : Screen("add_edit_session?sessionId={sessionId}", "Log Session", Icons.Default.AddCircle) {
        fun createRoute(sessionId: Int = -1): String = "add_edit_session?sessionId=$sessionId"
    }

    object SessionDetail : Screen("session_detail/{sessionId}", "Session Detail") {
        fun createRoute(sessionId: Int): String = "session_detail/$sessionId"
    }

    object Profile : Screen("user_profile", "Student Profile", Icons.Default.Person)

    object Login : Screen("login", "Student Login")

    object SignUp : Screen("signup", "Guided Sign Up")

    object WelcomeOnboarding : Screen("welcome_onboarding", "Welcome")
}
