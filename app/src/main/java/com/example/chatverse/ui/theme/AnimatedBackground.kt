package com.example.chatverse.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Animated deep-space background with floating glowing orbs.
 * Used on Welcome, Login, and Sign-Up screens.
 */
@Composable
fun AnimatedChatBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "bg")

    // Orb 1 – violet, top-left
    val o1x by transition.animateFloat(-0.05f, 0.08f,
        infiniteRepeatable(tween(7200, easing = FastOutSlowInEasing), RepeatMode.Reverse), "o1x")
    val o1y by transition.animateFloat(-0.03f, 0.06f,
        infiniteRepeatable(tween(9100, easing = LinearEasing), RepeatMode.Reverse), "o1y")

    // Orb 2 – cyan, bottom-right
    val o2x by transition.animateFloat(0.04f, -0.07f,
        infiniteRepeatable(tween(8600, easing = FastOutSlowInEasing), RepeatMode.Reverse), "o2x")
    val o2y by transition.animateFloat(0.02f, -0.09f,
        infiniteRepeatable(tween(6800, easing = LinearEasing), RepeatMode.Reverse), "o2y")

    // Orb 3 – magenta, centre
    val o3x by transition.animateFloat(-0.04f, 0.06f,
        infiniteRepeatable(tween(10400, easing = FastOutSlowInEasing), RepeatMode.Reverse), "o3x")
    val o3y by transition.animateFloat(0.05f, -0.05f,
        infiniteRepeatable(tween(11300, easing = LinearEasing), RepeatMode.Reverse), "o3y")

    // Orb 4 – indigo, top-right
    val o4x by transition.animateFloat(0.03f, -0.08f,
        infiniteRepeatable(tween(12100, easing = FastOutSlowInEasing), RepeatMode.Reverse), "o4x")
    val o4y by transition.animateFloat(-0.02f, 0.08f,
        infiniteRepeatable(tween(9700, easing = LinearEasing), RepeatMode.Reverse), "o4y")

    // Orb 5 – warm pink, bottom-left
    val o5x by transition.animateFloat(-0.06f, 0.04f,
        infiniteRepeatable(tween(13500, easing = FastOutSlowInEasing), RepeatMode.Reverse), "o5x")
    val o5y by transition.animateFloat(0.03f, -0.06f,
        infiniteRepeatable(tween(8200, easing = LinearEasing), RepeatMode.Reverse), "o5y")

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // ── Base gradient background ──────────────────────────────────
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF060B1A),
                        Color(0xFF0E1535),
                        Color(0xFF060B1A)
                    )
                )
            )

            // ── Orb 1 – deep violet ───────────────────────────────────────
            val c1 = Offset(w * (0.15f + o1x), h * (0.18f + o1y))
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x907C4DFF), Color.Transparent), c1, w * 0.65f
                ), radius = w * 0.65f, center = c1
            )

            // ── Orb 2 – electric cyan ─────────────────────────────────────
            val c2 = Offset(w * (0.85f + o2x), h * (0.76f + o2y))
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x7000E5FF), Color.Transparent), c2, w * 0.56f
                ), radius = w * 0.56f, center = c2
            )

            // ── Orb 3 – magenta ───────────────────────────────────────────
            val c3 = Offset(w * (0.70f + o3x), h * (0.44f + o3y))
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x60E040FB), Color.Transparent), c3, w * 0.46f
                ), radius = w * 0.46f, center = c3
            )

            // ── Orb 4 – indigo ────────────────────────────────────────────
            val c4 = Offset(w * (0.82f + o4x), h * (0.10f + o4y))
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x703D5AFE), Color.Transparent), c4, w * 0.43f
                ), radius = w * 0.43f, center = c4
            )

            // ── Orb 5 – warm rose ─────────────────────────────────────────
            val c5 = Offset(w * (0.22f + o5x), h * (0.88f + o5y))
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x55FF4081), Color.Transparent), c5, w * 0.50f
                ), radius = w * 0.50f, center = c5
            )

            // ── Subtle top vignette ───────────────────────────────────────
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0x18000000), Color.Transparent),
                    endY = h * 0.3f
                )
            )
        }
        content()
    }
}
