package com.deliamo.spywarecheck

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deliamo.spywarecheck.ui.actions.ActionFlowStubScreen
import com.deliamo.spywarecheck.ui.actions.DeviceAdminRemovalFlowScreen
import com.deliamo.spywarecheck.ui.navigation.Routes
import com.deliamo.spywarecheck.ui.screens.quickcheck.QuickCheckViewModel
import com.deliamo.spywarecheck.ui.screens.finding.FindingDetailScreen
import com.deliamo.spywarecheck.ui.screens.quickcheck.QuickCheckScreen
import com.deliamo.spywarecheck.ui.screens.result.ResultScreen
import com.deliamo.spywarecheck.ui.screens.safetygate.SafetyGateScreen
import com.deliamo.spywarecheck.ui.screens.scan.ScanScreen
import com.deliamo.spywarecheck.ui.screens.scan.ScanViewModel
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
    val startQuickCheck: () -> Unit = { navController.navigate(Routes.QUICK_CHECK) }
    val startScanGated: () -> Unit = { navController.navigate(Routes.SAFETY_GATE) }
    val goBack: () -> Unit = { navController.popBackStack() }
    val quickCheckVm: QuickCheckViewModel = viewModel()
    val scanVm: ScanViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartScreen(
                onStartQuickCheck = startQuickCheck,
                onStartScanGated = startScanGated,
                onOpenReport = {},
                onOpenActions = {},
                onQuickExit = quickExit,
            )
        }

        composable(Routes.QUICK_CHECK) {
            QuickCheckScreen(
                vm = quickCheckVm,
                onBack = goBack,
                onDone = { navController.navigate(Routes.RESULT) },
                onQuickExit = quickExit,
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                vm = quickCheckVm,
                onBack = goBack,
                onStartScanGated = { navController.navigate(Routes.SAFETY_GATE) },
                onQuickExit = quickExit,
            )
        }

        composable(Routes.SAFETY_GATE) {
            SafetyGateScreen(
                onContinue = {
                    navController.navigate(Routes.SCAN) { // TODO change to start scan screen
                        popUpTo(Routes.SAFETY_GATE) { inclusive = true }
                    }
                },
                onCancel = goBack, // Todo: go to Start Screen?
                onQuickExit = quickExit,
            )
        }

        composable(Routes.SCAN) {
            ScanScreen(
                onBack = goBack,
                onStartScan = { navController.navigate(Routes.SAFETY_GATE) },
                onOpenFinding = { id ->
                    navController.navigate(Routes.findingDetail(id)) },
                onQuickExit = quickExit,
                vm = scanVm
            )
        }

        composable(
            Routes.FINDING_DETAIL,
            arguments = listOf(navArgument("findingId") {
                type = NavType.StringType
            })
        ) { entry ->
            val findingId = entry.arguments?.getString("findingId") ?: ""
            FindingDetailScreen(
                findingId = findingId,
                onBack = { navController.popBackStack() },
                onQuickExit = quickExit,
                onOpenActionFlow = { flowId ->
                    navController.navigate(Routes.actionFlowStep(flowId, 0))
                },
                vm = scanVm
            )
        }

        composable(
            route = Routes.ACTION_FLOW,
            arguments = listOf(navArgument("flowId") { type = NavType.StringType })
        ) { entry ->
            val flowId = entry.arguments?.getString("flowId") ?: ""
            ActionFlowStubScreen(
                flowId = flowId,
                onBack = { navController.popBackStack() },
                onQuickExit = quickExit
            )
        }

        composable(
            route = Routes.ACTION_FLOW_STEP,
            arguments = listOf(
                navArgument("flowId") { type = NavType.StringType },
                navArgument("step") { type = NavType.IntType}
            )
        ) { entry ->
            val flowId = entry.arguments?.getString("flowId") ?: ""
            val step = entry.arguments?.getInt("step") ?: 0

            DeviceAdminRemovalFlowScreen(
                flowId = flowId,
                step = step,
                onBack = { navController.popBackStack() },
                onQuickExit = quickExit,
                onNavigateStep = { nextStep ->
                    navController.navigate(Routes.actionFlowStep(flowId, nextStep))
                },
                onFinish = { navController.navigate(Routes.SCAN) },
                scanVm = scanVm
            )
        }

    }
}
