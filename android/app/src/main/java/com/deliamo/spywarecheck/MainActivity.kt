package com.deliamo.spywarecheck

import android.net.Uri
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
import com.deliamo.spywarecheck.ui.screens.actions.ActionFlowStubScreen
import com.deliamo.spywarecheck.ui.screens.actions.GuidedRemovalFlowScreen
import com.deliamo.spywarecheck.ui.actions.flows.specs.AccessibilityFlowSpec
import com.deliamo.spywarecheck.ui.actions.flows.specs.BackgroundLocationFlowSpec
import com.deliamo.spywarecheck.ui.actions.flows.specs.DeviceAdminFlowSpec
import com.deliamo.spywarecheck.ui.actions.flows.specs.LocationFlowSpec
import com.deliamo.spywarecheck.ui.navigation.Routes
import com.deliamo.spywarecheck.ui.screens.actions.MeasuresScreen
import com.deliamo.spywarecheck.ui.screens.finding.FindingDetailScreen
import com.deliamo.spywarecheck.ui.screens.quickcheck.QuickCheckScreen
import com.deliamo.spywarecheck.ui.screens.quickcheck.QuickCheckViewModel
import com.deliamo.spywarecheck.ui.screens.report.ReportScreen
import com.deliamo.spywarecheck.ui.screens.result.ResultScreen
import com.deliamo.spywarecheck.ui.screens.safetygate.SafetyGatePresets
import com.deliamo.spywarecheck.ui.screens.safetygate.SafetyGateScreen
import com.deliamo.spywarecheck.ui.screens.scan.ScanScreen
import com.deliamo.spywarecheck.ui.screens.scan.ScanViewModel
import com.deliamo.spywarecheck.ui.screens.start.StartScreen
import com.deliamo.spywarecheck.ui.theme.SpywareCheckTheme
import com.deliamo.spywarecheck.ui.util.quickExitToBrowser


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      /*
        window.setFlags(
            // Disable Screenshots
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
       */
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
    val startScanGated: () -> Unit = { navController.navigate(Routes.SCAN_SAFETY_GATE) }
    val openActions: () -> Unit = { navController.navigate(Routes.MEASURES) }
    val goBack: () -> Unit = { navController.popBackStack() }
  val onOpenReport: () -> Unit = { navController.navigate(Routes.REPORT)}
    val quickCheckVm: QuickCheckViewModel = viewModel()
    val goHome: () -> Unit = {
        navController.navigate(Routes.START) {
            popUpTo(Routes.START) { inclusive = false }
            launchSingleTop = true
        }
    }
    val scanVm: ScanViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartScreen(
                onStartQuickCheck = startQuickCheck,
                onStartScanGated = startScanGated,
                onOpenReport = onOpenReport,
                onOpenActions = openActions,
                onQuickExit = quickExit,
            )
        }

        composable(Routes.QUICK_CHECK) {
            QuickCheckScreen(
                vm = quickCheckVm,
                onBack = goBack,
                onDone = { navController.navigate(Routes.RESULT) },
                onQuickExit = quickExit,
                onHome = goHome
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                vm = quickCheckVm,
                onBack = goBack,
                onStartScanGated = { navController.navigate(Routes.SCAN_SAFETY_GATE) },
                onQuickExit = quickExit,
                onHome = goHome
            )
        }

        composable(Routes.SCAN_SAFETY_GATE) {
            SafetyGateScreen(
                spec = SafetyGatePresets.Scan,
                onContinue = {
                    navController.navigate(Routes.SCAN) { // TODO change to start scan screen
                        popUpTo(Routes.SCAN_SAFETY_GATE) { inclusive = true }
                    }
                },
                onCancel = { navController.navigate(Routes.START) }, // Todo: go to Start Screen?
                onQuickExit = quickExit,
            )
        }

        composable(Routes.SCAN) {
            ScanScreen(
                onBack = goBack,
                onStartScan = { navController.navigate(Routes.SCAN_SAFETY_GATE) },
                onOpenFinding = { id ->
                    navController.navigate(Routes.findingDetail(id))
                },
                onQuickExit = quickExit,
                onHome = goHome,
                vm = scanVm,
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
                    navController.navigate(Routes.actionSafetyGate(flowId))
                },
                onHome = goHome,
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
                onQuickExit = quickExit,
                onHome = goHome
            )
        }

        composable(
            route = Routes.ACTION_FLOW_STEP,
            arguments = listOf(
                navArgument("flowId") { type = NavType.StringType },
                navArgument("step") { type = NavType.IntType }
            )
        ) { entry ->
            val flowId = entry.arguments?.getString("flowId") ?: ""
            val step = entry.arguments?.getInt("step") ?: 0

            val decoded = Uri.decode(flowId)

            val spec = when (decoded) {
                "device_admin_enabled" -> DeviceAdminFlowSpec
                "accessibility_enabled" -> AccessibilityFlowSpec
                "background_location_apps" -> BackgroundLocationFlowSpec
                "location_enabled" -> LocationFlowSpec
                else -> DeviceAdminFlowSpec // fallback
            }

            GuidedRemovalFlowScreen(
                spec = spec,
                flowId = flowId,
                step = step,
                onBack = { navController.popBackStack() },
                onQuickExit = quickExit,
                onNavigateStep = { nextStep ->
                    navController.navigate(Routes.actionFlowStep(flowId, nextStep))
                },
                onFinish = { navController.navigate(Routes.SCAN) },
                onHome = goHome,
                scanVm = scanVm
            )
        }
        composable(
            Routes.MEASURES) {
            MeasuresScreen(
                onBack = goBack,
                onQuickExit = quickExit,
                onStartScan = startScanGated,
                onOpenFlow = { flowId ->
                    navController.navigate(Routes.actionSafetyGate(flowId = flowId)) },
                onHome = goHome,
                scanVm = scanVm
            )
        }

        composable(
            route = Routes.ACTION_SAFETY_GATE,
            arguments = listOf(navArgument("flowId") { type = NavType.StringType })
        ) { entry ->
            val flowId = entry.arguments?.getString("flowId") ?: ""
            SafetyGateScreen(
                spec = SafetyGatePresets.Actions,
                onContinue = { navController.navigate(Routes.actionFlowStep(flowId, 0)) },
                onCancel = { navController.navigate(Routes.START) },
                onQuickExit = quickExit,
            )
        }

      composable(Routes.REPORT) {
        ReportScreen(
          onBack = { navController.popBackStack() },
          onQuickExit = quickExit,
          onOpenMeasures = { navController.navigate(Routes.MEASURES) },
          onHome = goHome,
          onOpenFinding = { id -> navController.navigate(Routes.findingDetail(id)) }
        )
      }

    }
}
