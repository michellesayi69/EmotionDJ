package com.example.emotiondj

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.emotiondj.ui.screens.HistoryScreen
import com.example.emotiondj.ui.screens.MainScreen
import com.example.emotiondj.ui.screens.SettingsScreen
import com.example.emotiondj.ui.screens.TutorialScreen
import com.example.emotiondj.ui.theme.EmotionDJTheme
import com.example.emotiondj.utils.NotificationHelper
import com.example.emotiondj.viewmodel.HistoryViewModel
import com.example.emotiondj.viewmodel.SettingsViewModel
import com.example.emotiondj.viewmodel.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        setContent {
            EmotionDJApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationHelper.cancelNotification(applicationContext)
    }
}

@Composable
fun EmotionDJApp() {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    EmotionDJTheme(darkTheme = isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "tutorial") {
                composable("tutorial") {
                    TutorialScreen(
                        onGetStarted = {
                            navController.navigate("main") {
                                popUpTo("tutorial") { inclusive = true }
                            }
                        },
                        onSkip = {
                            navController.navigate("main") {
                                popUpTo("tutorial") { inclusive = true }
                            }
                        }
                    )
                }
                composable("main") {
                    MainScreen(
                        onNavigateToHistory = { navController.navigate("history") },
                        onNavigateToSettings = { navController.navigate("settings") }
                    )
                }
                composable("history") {
                    val historyViewModel: HistoryViewModel = viewModel()
                    HistoryScreen(
                        onBack = { navController.popBackStack() },
                        viewModel = historyViewModel
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onReplayTutorial = {
                            navController.navigate("tutorial") {
                                popUpTo("main") { inclusive = false }
                            }
                        },
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}