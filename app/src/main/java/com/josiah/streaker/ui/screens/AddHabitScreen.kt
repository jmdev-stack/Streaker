package com.josiah.streaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.josiah.streaker.model.Habit
import com.josiah.streaker.ui.components.clickableNoRipple
import com.josiah.streaker.ui.theme.AppColors
import com.josiah.streaker.viewmodel.HabitViewModel

@Composable
fun AddHabitScreen(
    viewModel: HabitViewModel,
    onBack:    () -> Unit
) {
    var name      by remember { mutableStateOf("") }
    var category  by remember { mutableStateOf("General") }
    var emoji     by remember { mutableStateOf("🔥") }
    var nameError by remember { mutableStateOf(false) }

    val categories   = listOf("General", "Fitness", "Learning", "Health", "Mindfulness", "Finance")
    val emojiOptions = listOf("🔥", "🏃", "📚", "💧", "🧘", "💪", "✍️", "🎯", "🌱", "💊", "🎵", "🥗")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
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
                "New Habit",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = AppColors.TextPrimary,
                modifier   = Modifier.weight(1f),
                textAlign  = TextAlign.Center
            )
            Spacer(Modifier.size(48.dp))
        }

        // ── Scrollable Body ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Emoji Picker
            SectionLabel("Choose an Icon")
            Spacer(Modifier.height(10.dp))
            emojiOptions.chunked(6).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier              = Modifier.padding(bottom = 8.dp)
                ) {
                    row.forEach { e ->
                        val isSelected = e == emoji
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) AppColors.Ember.copy(.2f)
                                    else AppColors.Surface2
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) AppColors.Ember else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickableNoRipple { emoji = e },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(e, fontSize = 22.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Name Field
            SectionLabel("Habit Name")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value         = name,
                onValueChange = { name = it; nameError = false },
                placeholder   = { Text("e.g. Morning Run", color = AppColors.TextSecondary) },
                isError       = nameError,
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = AppColors.Ember,
                    unfocusedBorderColor    = Color.White.copy(alpha = 0.1f),
                    focusedTextColor        = AppColors.TextPrimary,
                    unfocusedTextColor      = AppColors.TextPrimary,
                    cursorColor             = AppColors.Ember,
                    errorBorderColor        = AppColors.Destructive,
                    focusedContainerColor   = AppColors.Surface1,
                    unfocusedContainerColor = AppColors.Surface1,
                    errorContainerColor     = AppColors.Surface1,
                )
            )
            if (nameError) {
                Text(
                    "Please enter a habit name",
                    color    = AppColors.Destructive,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Category Picker
            SectionLabel("Category")
            Spacer(Modifier.height(10.dp))
            categories.chunked(3).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier              = Modifier.padding(bottom = 8.dp)
                ) {
                    row.forEach { cat ->
                        val isSelected = cat == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
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
                                .clickableNoRipple { category = cat }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                cat,
                                fontSize   = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color      = if (isSelected) AppColors.Ember else AppColors.TextSecondary
                            )
                        }
                    }
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Live Preview
            if (name.isNotBlank()) {
                Spacer(Modifier.height(24.dp))
                SectionLabel("Preview")
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1A1A22))
                        .border(1.dp, Color.White.copy(.06f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier         = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.Surface2),
                            contentAlignment = Alignment.Center
                        ) { Text(emoji, fontSize = 22.sp) }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                name,
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = AppColors.TextPrimary
                            )
                            Text(
                                "🔥 1 day streak · $category",
                                fontSize = 12.sp,
                                color    = AppColors.TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // ── Save Button ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold))
                    )
                    .clickableNoRipple {
                        if (name.isBlank()) {
                            nameError = true
                        } else {
                            viewModel.addHabit(
                                Habit(
                                    name     = name.trim(),
                                    emoji    = emoji,
                                    category = category,
                                    streak   = 1
                                )
                            )
                            onBack()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔥", fontSize = 16.sp)
                    Text(
                        "Start Habit",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.SemiBold,
        color         = AppColors.TextSecondary,
        letterSpacing = 0.8.sp
    )
}