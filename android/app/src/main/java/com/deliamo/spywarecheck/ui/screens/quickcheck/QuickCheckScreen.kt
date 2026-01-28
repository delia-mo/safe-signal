package com.deliamo.spywarecheck.ui.screens.quickcheck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.FilledTonalButton
import com.deliamo.spywarecheck.domain.model.QuickAnswer
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.components.HomeFooterBar

@Composable
fun QuickCheckScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    onQuickExit: () -> Unit,
    onHome: () -> Unit,
    vm: QuickCheckViewModel
) {
    val ui by vm.ui.collectAsState()

    AppScaffold(
        title = "Quick Check",
        onQuickExit = onQuickExit,
        showBack = true,
        footer = { HomeFooterBar(onHome = onHome) },
        onBack = onBack
    ) { padding ->

        val q = ui.currentQuestion

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                // Progress
                Text(
                    text = ui.progressLabel,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    text = q?.text ?: "",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(18.dp))

                AnswerButtons { answer ->
                    vm.answerCurrent((answer))

                    if (ui.isLast) {
                        onDone()
                    } else {
                        vm.next()
                    }
                }

                Spacer(Modifier.height(18.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = { if (ui.canGoBack) vm.previous() else onBack() },
                    modifier = Modifier.align(Alignment.Start),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Letzte Frage") // TODO: formulierung
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Wenn du es nicht sicher weißt: tippe \"Unsicher\".",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }
}


@Composable
private fun AnswerButtons(
    onAnswer: (QuickAnswer) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FilledTonalButton(
            onClick = { onAnswer(QuickAnswer.YES) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ja")
        }
      FilledTonalButton(
            onClick = { onAnswer(QuickAnswer.NO) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nein")
        }
        OutlinedButton(
            onClick = { onAnswer(QuickAnswer.UNSURE) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unsicher")
        }
    }
}
