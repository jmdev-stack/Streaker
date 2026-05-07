package com.josiah.streaker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.josiah.streaker.ui.theme.AppColors
import com.josiah.streaker.ui.components.clickableNoRipple

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {

    val pages = listOf(
        Triple("🔥", "Build Streaks", "Small habits done daily\nlead to massive results."),
        Triple("📈", "Track Progress", "Watch your streaks grow\nday by day."),
        Triple("🏆", "Hit Milestones", "Celebrate every 7, 30\nand 100 day streak."),
    )

    var currentPage by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue  = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(40.dp))

            // Emoji
            Text(
                pages[currentPage].first,
                fontSize = (72 * emojiScale).sp
            )

            // Text content
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    pages[currentPage].second,
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Black,
                    color      = AppColors.TextPrimary,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    pages[currentPage].third,
                    fontSize  = 16.sp,
                    color     = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (index == currentPage) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (index == currentPage) AppColors.Ember
                                    else AppColors.Surface2
                                )
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold))
                        )
                        .then(
                            Modifier.clickableNoRipple {
                                if (currentPage < pages.size - 1) {
                                    currentPage++
                                } else {
                                    onFinish()
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (currentPage < pages.size - 1) "Next →" else "Let's Go 🔥",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}