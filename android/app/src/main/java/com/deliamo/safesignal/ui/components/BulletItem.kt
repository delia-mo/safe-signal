package com.deliamo.safesignal.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulletItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("•  ")
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
