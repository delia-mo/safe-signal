package com.deliamo.safesignal.ui.components

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppCard(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceTint
    )
  ) { content() }
}
