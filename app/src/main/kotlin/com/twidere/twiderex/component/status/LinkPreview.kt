package com.twidere.twiderex.component.status

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.viewmodel.LinkPreviewViewModel

@Composable
fun LinkPreview(url: String) {
    val viewModel = viewModel<LinkPreviewViewModel>()

    Box(
        modifier = Modifier
            .clickable(onClick = {

            })
            .clip(MaterialTheme.shapes.medium)
            .clipToBounds()
            .border(
                1.dp,
                AmbientContentColor.current.copy(alpha = 0.12f),
                MaterialTheme.shapes.medium,
            ),
    ) {

    }
}