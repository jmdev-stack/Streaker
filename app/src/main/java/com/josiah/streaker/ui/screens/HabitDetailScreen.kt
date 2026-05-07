package com.josiah.streaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import com.josiah.streaker.ui.components.clickableNoRipple
import com.josiah.streaker.ui.theme.AppColors
import com.josiah.streaker.viewmodel.HabitViewModel

@Composable
fun HabitDetailScreen(
    habitId:   Long,
    viewModel: HabitViewModel,
    onBack:    () -> Unit
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val habit = habits.find { it.id == habitId } ?: return

    var showEditDialog by remember { mutableStateOf(false) }
    var editName       by remember { mutableStateOf(habit.name) }
    var editEmoji      by remember { mutableStateOf(habit.emoji) }
    var editCategory   by remember { mutableStateOf(habit.category) }

    val emojiOptions = listOf("🔥", "🏃", "📚", "💧", "🧘", "💪", "✍️", "🎯", "🌱", "💊", "🎵", "🥗")
    val categories   = listOf("General", "Fitness", "Learning", "Health", "Mindfulness", "Finance")

    val milestones = listOf(7, 14, 30, 60, 100, 365)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar ─────────────────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = AppColors.TextSecondary)
            }
            Text(
                "Habit Details",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = AppColors.TextPrimary,
                modifier   = Modifier.weight(1f),
                textAlign  = TextAlign.Center
            )
            IconButton(onClick = { showEditDialog = true }) {
                Icon(Icons.Default.Edit, "Edit", tint = AppColors.Ember)
            }
        }

        // ── Hero Card ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1E1A14), Color(0xFF1A1410))
                    )
                )
                .border(
                    1.dp,
                    AppColors.Ember.copy(.2f),
                    RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(habit.emoji, fontSize = 56.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    habit.name,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Black,
                    color      = AppColors.TextPrimary,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    habit.category,
                    fontSize = 13.sp,
                    color    = AppColors.TextSecondary
                )
                Spacer(Modifier.height(20.dp))

                // Big streak number
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppColors.Surface2)
                        .padding(horizontal = 32.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${habit.streak}",
                            fontSize   = 48.sp,
                            fontWeight = FontWeight.Black,
                            color      = when {
                                habit.streak >= 30 -> AppColors.Gold
                                habit.streak >= 14 -> AppColors.Ember
                                else               -> AppColors.TextPrimary
                            }
                        )
                        Text(
                            "day streak 🔥",
                            fontSize = 14.sp,
                            color    = AppColors.TextSecondary
                        )
                    }
                }

                if (habit.completedToday) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.Success.copy(.15f))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "✓ Completed Today",
                            fontSize   = 13.sp,
                            color      = AppColors.Success,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Milestones ───────────────────────────────────────────────────────
        Text(
            "MILESTONES",
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = AppColors.TextSecondary,
            letterSpacing = 1.sp,
            modifier      = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            milestones.forEach { milestone ->
                val reached = habit.streak >= milestone
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (reached) AppColors.Ember.copy(.15f) else AppColors.Surface1
                        )
                        .border(
                            1.dp,
                            if (reached) AppColors.Ember.copy(.4f) else Color.White.copy(.06f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (reached) "🔥" else "🔒",
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "$milestone",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color      = if (reached) AppColors.Ember else AppColors.TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Quick Stats ──────────────────────────────────────────────────────
        Text(
            "STATS",
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            color         = AppColors.TextSecondary,
            letterSpacing = 1.sp,
            modifier      = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                label    = "Current Streak",
                value    = "${habit.streak} days",
                modifier = Modifier.weight(1f),
                accent   = AppColors.Ember
            )
            StatCard(
                label    = "Next Milestone",
                value    = "${milestones.firstOrNull { it > habit.streak } ?: "365+"} days",
                modifier = Modifier.weight(1f),
                accent   = AppColors.Gold
            )
        }

        Spacer(Modifier.height(32.dp))
    }

    // ── Edit Dialog ──────────────────────────────────────────────────────────
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor   = AppColors.Surface1,
            title = {
                Text("Edit Habit", color = AppColors.TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Emoji picker
                    Text("Icon", fontSize = 12.sp, color = AppColors.TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        emojiOptions.take(6).forEach { e ->
                            val isSelected = e == editEmoji
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) AppColors.Ember.copy(.2f)
                                        else AppColors.Surface2
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) AppColors.Ember else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickableNoRipple { editEmoji = e },
                                contentAlignment = Alignment.Center
                            ) { Text(e, fontSize = 18.sp) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        emojiOptions.drop(6).forEach { e ->
                            val isSelected = e == editEmoji
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) AppColors.Ember.copy(.2f)
                                        else AppColors.Surface2
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) AppColors.Ember else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickableNoRipple { editEmoji = e },
                                contentAlignment = Alignment.Center
                            ) { Text(e, fontSize = 18.sp) }
                        }
                    }

                    // Name field
                    Text("Name", fontSize = 12.sp, color = AppColors.TextSecondary)
                    OutlinedTextField(
                        value         = editName,
                        onValueChange = { editName = it },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = AppColors.Ember,
                            unfocusedBorderColor    = Color.White.copy(.1f),
                            focusedTextColor        = AppColors.TextPrimary,
                            unfocusedTextColor      = AppColors.TextPrimary,
                            focusedContainerColor   = AppColors.Surface2,
                            unfocusedContainerColor = AppColors.Surface2,
                            cursorColor             = AppColors.Ember,
                        )
                    )

                    // Category
                    Text("Category", fontSize = 12.sp, color = AppColors.TextSecondary)
                    categories.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { cat ->
                                val isSelected = cat == editCategory
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) AppColors.Ember.copy(.2f)
                                            else AppColors.Surface2
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) AppColors.Ember else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickableNoRipple { editCategory = cat }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        cat,
                                        fontSize = 11.sp,
                                        color    = if (isSelected) AppColors.Ember else AppColors.TextSecondary
                                    )
                                }
                            }
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold)))
                        .clickableNoRipple {
                            if (editName.isNotBlank()) {
                                viewModel.updateHabit(
                                    habit.copy(
                                        name     = editName.trim(),
                                        emoji    = editEmoji,
                                        category = editCategory
                                    )
                                )
                                showEditDialog = false
                            }
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = AppColors.TextSecondary)
                }
            }
        )
    }
}

