package woowacourse.shopping.ui.common.item

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.skeleton(isLoading: Boolean = true): Modifier {
    if (!isLoading) return this
    return composed {
        val transition = rememberInfiniteTransition(label = "skeleton")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "skeletonEffect",
        )

        drawWithContent {
            val shimmerColors =
                listOf(
                    Color.LightGray.copy(alpha = 0.9f),
                    Color.LightGray.copy(alpha = 0.5f),
                    Color.LightGray.copy(alpha = 0.9f),
                )
            drawRect(
                brush =
                    Brush.linearGradient(
                        colors = shimmerColors,
                        start = Offset.Zero,
                        end = Offset(translateAnimation, translateAnimation),
                    ),
            )
        }
    }
}
