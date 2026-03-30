package com.pepsigo.admin.screens.commonComponents

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun InlineRetryError(
    error: String,
    retryLoading: Boolean,
    onRetry: () -> Unit
){
    val rotation by rememberInfiniteTransition(label = "retry_rotation")
        .animateFloat(
            initialValue = 0f,
            targetValue = if (retryLoading) 360f else 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 900,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation_anim"
        )

    Row(
//        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Retry",
            tint = if (retryLoading)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    rotationZ = if (retryLoading) rotation else 0f
                }
                .clickable(
                    enabled = !retryLoading,
                    onClick = onRetry
                )
        )
    }

}