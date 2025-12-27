package com.deliamo.spywarecheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpywareCheckTheme() {
                SpywareCheckApp()
            }
        }
    }
}

@Composable
fun SpywareCheckApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartScreen(
                onStartQuickCheck = {},
                onQuickExit = {},
                debugContent = { DebugNavPanel(navController) }
            )
        }

        composable(Routes.QUICK_CHECK) {
            QuickCheckScreen(
                onDone = { navController.navigate(Routes.RESULT) }
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(

            )
        }

        composable(Routes.SAFETY_GATE) {
            SafetyGateScreen(
                onContinue = { navController.navigate(Routes.SCAN) },
                onCancel = { navController.popBackStack() },
            )
        }

        composable(Routes.SCAN) {
            ScanScreen(
                onOpenFinding = { navController.navigate(Routes.FINDING) },
            )
        }

        composable(Routes.FINDING) {
            FindingDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }

    }
}
