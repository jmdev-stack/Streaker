package com.josiah.streaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.josiah.streaker.NotificationScheduler
import com.josiah.streaker.ui.components.clickableNoRipple
import com.josiah.streaker.ui.theme.AppColors
import com.josiah.streaker.viewmodel.HabitViewModel

@Composable
fun SettingsScreen(
    onBack:    () -> Unit,
    viewModel: HabitViewModel
) {
    val context     = LocalContext.current
    val user        = viewModel.getCurrentUser()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var dailyReminderEnabled by remember { mutableStateOf(true) }
    var streakAlertEnabled   by remember { mutableStateOf(false) }

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
                "Settings",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = AppColors.TextPrimary,
                modifier   = Modifier.weight(1f),
                textAlign  = TextAlign.Center
            )
            Spacer(Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            // ── Profile Section ──────────────────────────────────────────────
            SettingsSectionLabel("PROFILE")
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Surface1)
                    .border(1.dp, Color.White.copy(.06f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppColors.Ember.copy(.2f))
                            .border(1.dp, AppColors.Ember.copy(.3f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user?.displayName?.firstOrNull()?.toString() ?: "🔥",
                            fontSize   = 26.sp,
                            fontWeight = FontWeight.Black,
                            color      = AppColors.TextPrimary
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            user?.displayName ?: "Streaker User",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = AppColors.TextPrimary
                        )
                        Text(
                            user?.email ?: "Building habits daily",
                            fontSize = 13.sp,
                            color    = AppColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Notifications Section ────────────────────────────────────────
            SettingsSectionLabel("NOTIFICATIONS")
            Spacer(Modifier.height(10.dp))

            SettingsCard {
                SettingsToggleRow(
                    icon     = Icons.Default.Notifications,
                    title    = "Enable Notifications",
                    subtitle = "Get reminders to complete habits",
                    checked  = notificationsEnabled,
                    onToggle = {
                        notificationsEnabled = it
                        NotificationScheduler.scheduleDailyReminder(context, it && dailyReminderEnabled)
                        NotificationScheduler.scheduleStreakAlert(context, it && streakAlertEnabled)
                    }
                )
                SettingsDivider()
                SettingsToggleRow(
                    icon     = Icons.Default.DateRange,
                    title    = "Daily Reminder",
                    subtitle = "Remind me every day at 9:00 AM",
                    checked  = dailyReminderEnabled,
                    onToggle = {
                        dailyReminderEnabled = it
                        NotificationScheduler.scheduleDailyReminder(context, it && notificationsEnabled)
                    },
                    enabled  = notificationsEnabled
                )
                SettingsDivider()
                SettingsToggleRow(
                    icon     = Icons.Default.Warning,
                    title    = "Streak at Risk Alert",
                    subtitle = "Alert me if I haven't completed by 8 PM",
                    checked  = streakAlertEnabled,
                    onToggle = {
                        streakAlertEnabled = it
                        NotificationScheduler.scheduleStreakAlert(context, it && notificationsEnabled)
                    },
                    enabled  = notificationsEnabled
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── App Section ──────────────────────────────────────────────────
            SettingsSectionLabel("APP")
            Spacer(Modifier.height(10.dp))

            SettingsCard {
                SettingsToggleRow(
                    icon     = Icons.Default.Star,
                    title    = "Dark Mode",
                    subtitle = "Switch between dark and light theme",
                    checked  = isDarkTheme,
                    onToggle = { viewModel.toggleTheme() }
                )
                SettingsDivider()
                SettingsActionRow(
                    icon     = Icons.Default.Refresh,
                    title    = "Reset Today's Progress",
                    subtitle = "Mark all habits as not done today",
                    tint     = AppColors.Ember
                )
                SettingsDivider()
                SettingsActionRow(
                    icon     = Icons.Default.Info,
                    title    = "About Streaker",
                    subtitle = "Version 1.0.0"
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Danger Zone ──────────────────────────────────────────────────
            SettingsSectionLabel("DANGER ZONE")
            Spacer(Modifier.height(10.dp))

            SettingsCard {
                SettingsActionRow(
                    icon     = Icons.Default.ExitToApp,
                    title    = "Sign Out",
                    subtitle = "You will need to sign in again",
                    tint     = AppColors.Ember,
                    onClick  = {
                        viewModel.signOut { onBack() }
                    }
                )
                SettingsDivider()
                SettingsActionRow(
                    icon     = Icons.Default.Delete,
                    title    = "Delete All Habits",
                    subtitle = "This cannot be undone",
                    tint     = AppColors.Destructive
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Settings Components ────────────────────────────────────────────────────────

@Composable
fun SettingsSectionLabel(text: String) {
    Text(
        text,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.SemiBold,
        color         = AppColors.TextSecondary,
        letterSpacing = 1.sp
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface1)
            .border(1.dp, Color.White.copy(.06f), RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 16.dp)
            .background(Color.White.copy(.06f))
    )
}

@Composable
fun SettingsToggleRow(
    icon:     ImageVector,
    title:    String,
    subtitle: String,
    checked:  Boolean,
    onToggle: (Boolean) -> Unit,
    enabled:  Boolean = true
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppColors.Surface2),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon, null,
                tint     = if (enabled) AppColors.Ember else AppColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = if (enabled) AppColors.TextPrimary else AppColors.TextSecondary
            )
            Text(subtitle, fontSize = 12.sp, color = AppColors.TextSecondary)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            enabled         = enabled,
            colors          = SwitchDefaults.colors(
                checkedThumbColor           = Color.White,
                checkedTrackColor           = AppColors.Ember,
                uncheckedThumbColor         = AppColors.TextSecondary,
                uncheckedTrackColor         = AppColors.Surface2,
                disabledCheckedTrackColor   = AppColors.Surface2,
                disabledUncheckedTrackColor = AppColors.Surface2,
            )
        )
    }
}

@Composable
fun SettingsActionRow(
    icon:     ImageVector,
    title:    String,
    subtitle: String,
    tint:     Color      = AppColors.TextSecondary,
    onClick:  () -> Unit = {}
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickableNoRipple { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = tint)
            Text(subtitle, fontSize = 12.sp, color = AppColors.TextSecondary)
        }
        Icon(
            Icons.Default.KeyboardArrowRight, null,
            tint     = AppColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}