package com.josiah.streaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.josiah.streaker.model.Habit
import com.josiah.streaker.ui.components.CelebrationOverlay
import com.josiah.streaker.ui.components.clickableNoRipple
import com.josiah.streaker.ui.theme.AppColors
import com.josiah.streaker.viewmodel.HabitViewModel

@Composable
fun HomeScreen(
    viewModel:       HabitViewModel,
    onAddClick:      () -> Unit,
    onHabitClick:    (Habit) -> Unit,
    onSettingsClick: () -> Unit
) {
    val habits            by viewModel.habits.collectAsStateWithLifecycle()
    val celebrationStreak by viewModel.celebrationStreak.collectAsStateWithLifecycle()
    val isLoading         by viewModel.isLoading.collectAsStateWithLifecycle()

    val totalStreak   = habits.sumOf { it.streak }
    val doneToday     = habits.count { it.completedToday }
    val longestStreak = habits.maxOfOrNull { it.streak } ?: 0

    // Loading state
    if (isLoading) {
        Box(
            modifier         = Modifier
                .fillMaxSize()
                .background(AppColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = AppColors.Ember)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Loading habits...",
                    color    = AppColors.TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {

            // ── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Streaker",
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Black,
                        color      = AppColors.TextPrimary
                    )
                    Text(
                        "Keep the fire alive 🔥",
                        fontSize = 13.sp,
                        color    = AppColors.TextSecondary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppColors.Surface1)
                            .border(1.dp, Color.White.copy(.06f), RoundedCornerShape(14.dp))
                            .clickableNoRipple { onSettingsClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Settings, "Settings",
                            tint     = AppColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold))
                            )
                            .clickableNoRipple { onAddClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add, "Add habit",
                            tint     = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ── Stats Row ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard("Total 🔥", "$totalStreak", Modifier.weight(1f))
                StatCard(
                    label    = "Today",
                    value    = "$doneToday / ${habits.size}",
                    modifier = Modifier.weight(1f),
                    accent   = if (habits.isNotEmpty() && doneToday == habits.size) AppColors.Success else null
                )
                StatCard("Best", "$longestStreak 🏆", Modifier.weight(1f), accent = AppColors.Gold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (habits.isEmpty()) {
                EmptyState(onAddClick)
            } else {
                Text(
                    "MY HABITS",
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = AppColors.TextSecondary,
                    letterSpacing = 1.sp,
                    modifier      = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier            = Modifier.fillMaxSize()
                ) {
                    items(habits, key = { it.id }) { habit ->
                        HabitCard(
                            habit      = habit,
                            onComplete = { viewModel.completeHabit(habit) },
                            onDelete   = { viewModel.deleteHabit(habit) },
                            onClick    = { onHabitClick(habit) }
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }

        // ── Celebration Overlay ──────────────────────────────────────────────
        celebrationStreak?.let { streak ->
            CelebrationOverlay(
                streak    = streak,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }
    }
}

@Composable
fun HabitCard(
    habit:      Habit,
    onComplete: () -> Unit,
    onDelete:   () -> Unit,
    onClick:    () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (habit.completedToday) Color(0xFF141A17) else Color(0xFF1A1A22)
            )
            .border(
                1.dp,
                if (habit.completedToday) AppColors.Success.copy(.3f)
                else Color.White.copy(.06f),
                RoundedCornerShape(16.dp)
            )
            .clickableNoRipple { onClick() }
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Surface2),
                contentAlignment = Alignment.Center
            ) {
                Text(habit.emoji, fontSize = 22.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.name,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = AppColors.TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val streakColor = when {
                        habit.streak >= 30 -> AppColors.Gold
                        habit.streak >= 14 -> AppColors.Ember
                        habit.streak >= 7  -> AppColors.EmberLight
                        else               -> AppColors.TextSecondary
                    }
                    Text("🔥 ${habit.streak} day streak", fontSize = 12.sp, color = streakColor)
                    if (habit.completedToday) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AppColors.Success.copy(.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "Done ✓",
                                fontSize   = 10.sp,
                                color      = AppColors.Success,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!habit.completedToday) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AppColors.Ember.copy(.15f))
                            .clickableNoRipple { onComplete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check, "Complete",
                            tint     = AppColors.Ember,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AppColors.Destructive.copy(.12f))
                        .clickableNoRipple { showConfirm = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete, "Delete",
                        tint     = AppColors.Destructive,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (showConfirm) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A0A10)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier              = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        "Delete \"${habit.name}\"?",
                        color    = AppColors.TextPrimary,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { showConfirm = false }) {
                        Text("Keep", color = AppColors.TextSecondary, fontSize = 13.sp)
                    }
                    Button(
                        onClick  = onDelete,
                        colors   = ButtonDefaults.buttonColors(containerColor = AppColors.Destructive),
                        shape    = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Delete", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    label:    String,
    value:    String,
    modifier: Modifier = Modifier,
    accent:   Color?   = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface1)
            .border(1.dp, Color.White.copy(.06f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(label, fontSize = 10.sp, color = AppColors.TextSecondary, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accent ?: AppColors.TextPrimary)
        }
    }
}

@Composable
fun EmptyState(onAdd: () -> Unit) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🌱", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "No habits yet",
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            color      = AppColors.TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Start building your streak today.\nEvery fire begins with a spark.",
            fontSize  = 14.sp,
            color     = AppColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold))
                )
                .clickableNoRipple { onAdd() }
                .padding(horizontal = 28.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Add First Habit", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}