package com.deliamo.spywarecheck

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deliamo.spywarecheck.ui.navigation.Routes
import com.deliamo.spywarecheck.ui.screens.start.DebugNavPanel
import com.deliamo.spywarecheck.ui.screens.start.FindingDetailScreen
import com.deliamo.spywarecheck.ui.screens.start.QuickCheckScreen
import com.deliamo.spywarecheck.ui.screens.start.ResultScreen
import com.deliamo.spywarecheck.ui.screens.start.SafetyGateScreen
import com.deliamo.spywarecheck.ui.screens.start.ScanScreen
import com.deliamo.spywarecheck.ui.screens.start.StartScreen
import com.deliamo.spywarecheck.ui.theme.SpywareCheckTheme
import com.deliamo.spywarecheck.ui.util.quickExitToBrowser


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            // Disable Screenshots
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setContent {
            SpywareCheckTheme {
                SpywareCheckApp()
            }
        }
    }
}

@Composable
fun SpywareCheckApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val quickExit: () -> Unit = { quickExitToBrowser(context) }
    val goBack: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartScreen(
                onStartQuickCheck = {},
                onQuickExit = quickExit,
                // TODO remove debug content
                debugContent = { DebugNavPanel(navController) }
            )
        }

        composable(Routes.QUICK_CHECK) {
            QuickCheckScreen(
                onBack = goBack,
                onDone = { navController.navigate(Routes.RESULT) },
                onQuickExit = quickExit,
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                onBack = goBack,
                onQuickExit = quickExit,
            )
        }

        composable(Routes.SAFETY_GATE) {
            SafetyGateScreen(
                onBack = goBack,
                onContinue = {
                    navController.navigate(Routes.SCAN) {
                        popUpTo(Routes.SAFETY_GATE) { inclusive = true }
                    }
                },
                // TODO do something else than just go back
                onCancel = goBack,
                onQuickExit = quickExit,
            )
        }

        composable(Routes.SCAN) {
            ScanScreen(
                onBack = goBack,
                onOpenFinding = { navController.navigate(Routes.FINDING) },
                onQuickExit = quickExit,
            )
        }

        composable(Routes.FINDING) {
            FindingDetailScreen(
                onBack = goBack,
                onQuickExit = quickExit
            )
        }

    }
}
