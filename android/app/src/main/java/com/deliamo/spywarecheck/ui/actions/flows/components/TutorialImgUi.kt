package com.deliamo.spywarecheck.ui.actions.flows.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class TutorialImgUi(val resId: Int, val desc: String)

@Composable
fun TutorialImagesRow(
    images: List<TutorialImgUi>,
    onQuickExit: () -> Unit,           // ✅ important for safety
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf<TutorialImgUi?>(null) }

    // Thumbnails
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        images.forEach { img ->
            Image(
                painter = painterResource(img.resId),
                contentDescription = img.desc,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(9f / 16f)
                    .clickable { selected = img }
            )
        }
    }

    // Viewer
    val s = selected
    if (s != null) {
        Dialog(
            onDismissRequest = { selected = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Card is NOT fullscreen -> outside exists -> click outside dismiss works
                Surface(
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 680.dp) // adjust if you want
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header row: Quick Exit + Close
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Beispiel", style = MaterialTheme.typography.titleMedium)

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = onQuickExit) {
                                    Text("Quick Exit")
                                }
                                IconButton(onClick = { selected = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Schließen")
                                }
                            }
                        }

                        // Big image
                        Image(
                            painter = painterResource(s.resId),
                            contentDescription = s.desc,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = true)
                        )

                        // Caption
                        Text(s.desc, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
