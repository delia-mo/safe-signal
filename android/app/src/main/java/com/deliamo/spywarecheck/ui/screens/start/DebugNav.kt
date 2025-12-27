package com.deliamo.spywarecheck.ui.screens.start

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.deliamo.spywarecheck.ui.navigation.Routes

@Composable
fun DebugNavPanel(navController: NavHostController) {
    Column {
        Text("Debug Navigation")
        Button(onClick = { navController.navigate(Routes.QUICK_CHECK) }) { Text("QuickCheck") }
        Button(onClick = { navController.navigate(Routes.RESULT) }) { Text("Result") }
        Button(onClick = { navController.navigate(Routes.SCAN) }) { Text("Scan") }
        Button(onClick = { navController.navigate(Routes.SAFETY_GATE) }) { Text("Safety Gate") }
        Button(onClick = { navController.navigate(Routes.FINDING) }) { Text("Finding") }
    }
}