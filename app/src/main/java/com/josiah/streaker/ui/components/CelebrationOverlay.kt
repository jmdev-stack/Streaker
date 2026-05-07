package com.josiah.streaker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.josiah.streaker.ui.theme.AppColors
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun CelebrationOverlay(
    streak:    Int,
    onDismiss: () -> Unit
) {
    val milestone = listOf(7, 14, 30, 60, 100, 365).firstOrNull { it == streak }
    if (milestone == null) return

    val milestoneText = when (milestone) {
        7   -> "One Week! 🔥"
        14  -> "Two Weeks! 💪"
        30  -> "One Month! 🏆"
        60  -> "Two Months! ⚡"
        100 -> "100 Days! 👑"
        365 -> "One Year! 🌟"
        else -> "$milestone Days!"
    }

    val message = when (milestone) {
        7   -> "You're building a real habit!"
        14  -> "Two weeks strong, keep it up!"
        30  -> "A full month — you're unstoppable!"
        60  -> "Two months of pure dedication!"
        100 -> "100 days — you're a legend!"
        365 -> "A full year — absolutely incredible!"
        else -> "Amazing milestone reached!"
    }

    // Auto dismiss after 4 seconds
    LaunchedEffect(Unit) {
        delay(4000)
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "celebrate")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 0.92f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val parties = listOf(
        Party(
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(80),
            position = Position.Relative(0.5, 0.0),
            spread = 180,
            colors = listOf(
                0xFFFF6B2B.toInt(),
                0xFFFFCC44.toInt(),
                0xFF44DD88.toInt(),
                0xFF4488FF.toInt(),
                0xFFFF44AA.toInt(),
            )
        ),
        Party(
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(40),
            position = Position.Relative(0.0, 0.5),
            spread = 90,
            colors = listOf(
                0xFFFF6B2B.toInt(),
                0xFFFFCC44.toInt(),
                0xFF44DD88.toInt(),
            )
        ),
        Party(
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(40),
            position = Position.Relative(1.0, 0.5),
            spread = 90,
            colors = listOf(
                0xFFFF6B2B.toInt(),
                0xFFFFCC44.toInt(),
                0xFF44DD88.toInt(),
            )
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier         = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Konfetti layer
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties  = parties
            )

            // Card
            AnimatedVisibility(
                visible = true,
                enter   = scaleIn(initialScale = 0.6f) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .padding(32.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(AppColors.Surface1)
                        .clickableNoRipple { onDismiss() }
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "🏆",
                            fontSize = (64 * scale).sp
                        )
                        Text(
                            milestoneText,
                            fontSize   = 26.sp,
                            fontWeight = FontWeight.Black,
                            color      = AppColors.Gold,
                            textAlign  = TextAlign.Center
                        )
                        Text(
                            message,
                            fontSize  = 15.sp,
                            color     = AppColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.Ember.copy(.15f))
                                .clickableNoRipple { onDismiss() }
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text(
                                "Keep Going 🔥",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color      = AppColors.Ember
                            )
                        }
                    }
                }
            }
        }
    }
}