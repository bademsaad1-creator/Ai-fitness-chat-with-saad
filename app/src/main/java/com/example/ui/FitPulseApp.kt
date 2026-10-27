package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.AiWorkoutGeneratorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NutritionScreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.Obsidian
import com.example.ui.theme.SurfaceCardHighlight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel

enum class FitPulseScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    GENERATOR("AI Plan", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    COACH("AI Coach", Icons.Filled.SmartToy, Icons.Outlined.SmartToy),
    NUTRITION("Macros", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    HISTORY("Progress", Icons.Filled.History, Icons.Outlined.History)
}

@Composable
fun FitPulseApp(
    viewModel: FitnessViewModel = viewModel()
) {
    val activeSession by viewModel.activeSession.collectAsState()
    var currentScreen by remember { mutableStateOf(FitPulseScreen.HOME) }

    // If active session is running, present the active workout screen
    val currentActiveSession = activeSession
    if (currentActiveSession != null) {
        BackHandler {
            // Intercept back button during active workout so session isn't accidentally closed
        }
        ActiveWorkoutScreen(
            viewModel = viewModel,
            session = currentActiveSession
        )
        return
    }

    // Secondary screen back handler to return to Home
    BackHandler(enabled = currentScreen != FitPulseScreen.HOME) {
        currentScreen = FitPulseScreen.HOME
    }

    Scaffold(
        containerColor = Obsidian,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("fitpulse_bottom_nav")
            ) {
                FitPulseScreen.entries.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Obsidian,
                            selectedTextColor = NeonEmerald,
                            indicatorColor = NeonEmerald,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                FitPulseScreen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToGenerator = { currentScreen = FitPulseScreen.GENERATOR },
                    onNavigateToCoach = { currentScreen = FitPulseScreen.COACH },
                    onNavigateToNutrition = { currentScreen = FitPulseScreen.NUTRITION },
                    onNavigateToHistory = { currentScreen = FitPulseScreen.HISTORY }
                )

                FitPulseScreen.GENERATOR -> AiWorkoutGeneratorScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = FitPulseScreen.HOME },
                    onStartWorkout = { routine ->
                        viewModel.startWorkout(routine.routineTitle, routine.difficultyLevel, routine.exercises)
                    }
                )

                FitPulseScreen.COACH -> AiCoachScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = FitPulseScreen.HOME }
                )

                FitPulseScreen.NUTRITION -> NutritionScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = FitPulseScreen.HOME }
                )

                FitPulseScreen.HISTORY -> HistoryScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = FitPulseScreen.HOME }
                )
            }
        }
    }
}
