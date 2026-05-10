package com.josiah.streaker

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.josiah.streaker.model.Habit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StreakerWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val habits        = fetchHabits()
        val doneToday     = habits.count { it.completedToday }
        val totalHabits   = habits.size
        val totalStreak   = habits.sumOf { it.streak }
        val longestStreak = habits.maxOfOrNull { it.streak } ?: 0

        provideContent {
            WidgetContent(
                doneToday     = doneToday,
                totalHabits   = totalHabits,
                totalStreak   = totalStreak,
                longestStreak = longestStreak
            )
        }
    }

    suspend fun fetchHabits(): List<Habit> {
        return try {
            val userId   = Firebase.auth.currentUser?.uid ?: "test_user"
            val snapshot = Firebase.firestore
                .collection("users")
                .document(userId)
                .collection("habits")
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    Habit(
                        id             = doc.getLong("id") ?: 0L,
                        name           = doc.getString("name") ?: "",
                        emoji          = doc.getString("emoji") ?: "🔥",
                        streak         = doc.getLong("streak")?.toInt() ?: 1,
                        completedToday = doc.getBoolean("completedToday") ?: false,
                        category       = doc.getString("category") ?: "General"
                    )
                } catch (_: Exception) { null }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        fun forceUpdate(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val manager = GlanceAppWidgetManager(context)
                    val widget  = StreakerWidget()
                    val glanceIds = manager.getGlanceIds(StreakerWidget::class.java)
                    glanceIds.forEach { glanceId ->
                        widget.update(context, glanceId)
                    }
                } catch (_: Exception) { }
            }
        }
    }
}

@Composable
fun WidgetContent(
    doneToday:     Int,
    totalHabits:   Int,
    totalStreak:   Int,
    longestStreak: Int
) {
    val white = androidx.glance.color.ColorProvider(
        day   = Color(0xFFF2F2F4),
        night = Color(0xFFF2F2F4)
    )
    val grey = androidx.glance.color.ColorProvider(
        day   = Color(0xFF8888A0),
        night = Color(0xFF8888A0)
    )

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12))
            .padding(16.dp)
            .appWidgetBackground()
    ) {
        // Header
        Text(
            "🔥 Streaker",
            style = TextStyle(
                color      = white,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Stats row
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            WidgetStat(
                value    = "$doneToday/$totalHabits",
                label    = "Today",
                modifier = GlanceModifier.defaultWeight()
            )
            WidgetStat(
                value    = "$totalStreak 🔥",
                label    = "Total",
                modifier = GlanceModifier.defaultWeight()
            )
            WidgetStat(
                value    = "$longestStreak 🏆",
                label    = "Best",
                modifier = GlanceModifier.defaultWeight()
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        Button(
            text     = "Open Streaker →",
            onClick  = actionStartActivity<MainActivity>(),
            modifier = GlanceModifier.fillMaxWidth()
        )
    }
}

@Composable
fun WidgetStat(
    value:    String,
    label:    String,
    modifier: GlanceModifier = GlanceModifier
) {
    val white = androidx.glance.color.ColorProvider(
        day   = Color(0xFFF2F2F4),
        night = Color(0xFFF2F2F4)
    )
    val grey = androidx.glance.color.ColorProvider(
        day   = Color(0xFF8888A0),
        night = Color(0xFF8888A0)
    )

    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            style = TextStyle(
                color      = white,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            label,
            style = TextStyle(
                color    = grey,
                fontSize = 11.sp
            )
        )
    }
}

class StreakerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = StreakerWidget()
}