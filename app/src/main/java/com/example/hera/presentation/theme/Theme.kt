package com.example.hera.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme

@Composable
fun HeraTheme(
        content: @Composable () -> Unit
) {
    /**
     * Empty theme to customize for your app.
     * See: https://developer.android.com/jetpack/compose/designsystems/custom
     */
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = Color(0xFFE91E63),
            secondary = Color(0xFFE91E63)
        ),
            content = content
    )
}