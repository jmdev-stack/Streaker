package com.josiah.streaker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.josiah.streaker.ui.theme.AppColors
import java.util.Calendar

// ── Greeting based on time of day ─────────────────────────────────────────────
@Composable
fun GreetingText(displayName: String?) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        hour < 21 -> "Good Evening"
        else      -> "Good Night"
    }
    val emoji = when {
        hour < 12 -> "🌅"
        hour < 17 -> "☀️"
        hour < 21 -> "🌆"
        else      -> "🌙"
    }
    val name = displayName?.split(" ")?.firstOrNull() ?: "Champ"

    Column {
        Text(
            "Streaker",
            fontSize   = 28.sp,
            fontWeight = FontWeight.Black,
            color      = AppColors.TextPrimary
        )
        Text(
            "$greeting, $name $emoji",
            fontSize = 13.sp,
            color    = AppColors.TextSecondary
        )
    }
}

// ── Daily motivational quotes ─────────────────────────────────────────────────
@Composable
fun DailyQuote() {
    val quotes = listOf(
        "Small steps every day lead to big results. 💪",
        "Consistency is the key to all success. 🔑",
        "You don't have to be great to start. 🌱",
        "Every day is a chance to be better. ✨",
        "Progress, not perfection. 🎯",
        "Your future self will thank you. 🙏",
        "Show up today. That's all it takes. 🔥",
        "One habit at a time changes everything. 🌊",
        "Discipline is freedom. 🦅",
        "The streak continues because YOU showed up. 👑",
    )

    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val quote     = quotes[dayOfYear % quotes.size]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        AppColors.Ember.copy(.08f),
                        AppColors.Gold.copy(.05f)
                    )
                )
            )
            .border(1.dp, AppColors.Ember.copy(.15f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("💬", fontSize = 16.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                quote,
                fontSize   = 12.sp,
                color      = AppColors.TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

// ── Today's progress bar ──────────────────────────────────────────────────────
@Composable
fun TodayProgressBar(done: Int, total: Int) {
    if (total == 0) return

    val progress     = done.toFloat() / total.toFloat()
    val animProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(800, easing = EaseOutCubic),
        label         = "progress"
    )
    val allDone = done == total

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface1)
            .border(1.dp, Color.White.copy(.06f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    if (allDone) "All done today! 🎉" else "Today's Progress",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (allDone) AppColors.Success else AppColors.TextPrimary
                )
                Text(
                    "$done / $total",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (allDone) AppColors.Success else AppColors.Ember
                )
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AppColors.Surface2)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (allDone)
                                Brush.linearGradient(listOf(AppColors.Success, AppColors.Success))
                            else
                                Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold))
                        )
                )
            }
        }
    }
}

// ── Streak fire intensity ─────────────────────────────────────────────────────
fun streakEmoji(streak: Int): String {
    return when {
        streak >= 100 -> "🔥🔥🔥"
        streak >= 30  -> "🔥🔥"
        else          -> "🔥"
    }
}

fun streakFontSize(streak: Int): Float {
    return when {
        streak >= 100 -> 16f
        streak >= 30  -> 14f
        streak >= 7   -> 13f
        else          -> 12f
    }
}

// ── Pulsing complete button ───────────────────────────────────────────────────
@Composable
fun PulsingCompleteButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.12f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val haptic = rememberHaptic()

    Box(
        modifier = Modifier
            .size(38.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(AppColors.Ember.copy(.2f))
            .border(1.dp, AppColors.Ember.copy(.4f), CircleShape)
            .clickableNoRipple {
                haptic()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Check,
            "Complete",
            tint     = AppColors.Ember,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── Animated streak number ────────────────────────────────────────────────────
@Composable
fun AnimatedStreakText(streak: Int, color: Color) {
    val animatedStreak by animateIntAsState(
        targetValue   = streak,
        animationSpec = tween(600, easing = EaseOutCubic),
        label         = "streakAnim"
    )
    Text(
        "${streakEmoji(streak)} $animatedStreak day streak",
        fontSize   = streakFontSize(streak).sp,
        color      = color,
        fontWeight = FontWeight.Medium
    )
}