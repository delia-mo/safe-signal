package com.deliamo.spywarecheck.ui.screens.start

import android.widget.Space
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.navigation.Routes
import com.deliamo.spywarecheck.ui.theme.SpywareCheckTheme

@Composable
fun StartScreen(
    onStartQuickCheck: () -> Unit,
    onQuickExit: () -> Unit,
    debugContent: (@Composable () -> Unit)? = null
    ) {
    AppScaffold(title = "Sicherheits-Check", onQuickExit = onQuickExit) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Hier bekommst du Hinweise auf mögliche Spyware/Dual-Use-Apps.")
            Spacer(Modifier.height(12.dp))
            Button(onClick = onStartQuickCheck) { Text("Quick Check starten") }

            debugContent?.let {
                Spacer(Modifier.height(24.dp))
                it()
            }
        }
    }
}
