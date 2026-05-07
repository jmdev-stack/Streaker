package com.josiah.streaker

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.josiah.streaker.model.Habit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class HabitRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val auth = Firebase.auth

    private val userId: String
        get() = auth.currentUser?.uid ?: "test_user"

    private val habitsCollection
        get() = db.collection("users").document(userId).collection("habits")

    fun getHabits(): Flow<List<Habit>> = callbackFlow {
        val listener = habitsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val habits = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Habit(
                            id             = doc.getLong("id") ?: 0L,
                            name           = doc.getString("name") ?: "",
                            emoji          = doc.getString("emoji") ?: "🔥",
                            streak         = doc.getLong("streak")?.toInt() ?: 1,
                            completedToday = doc.getBoolean("completedToday") ?: false,
                            category       = doc.getString("category") ?: "General"
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(habits)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addHabit(habit: Habit) {
        habitsCollection.document(habit.id.toString()).set(
            mapOf(
                "id"             to habit.id,
                "name"           to habit.name,
                "emoji"          to habit.emoji,
                "streak"         to habit.streak,
                "completedToday" to habit.completedToday,
                "category"       to habit.category
            )
        ).await()
    }

    suspend fun deleteHabit(habit: Habit) {
        habitsCollection.document(habit.id.toString()).delete().await()
    }

    suspend fun updateHabit(habit: Habit) {
        habitsCollection.document(habit.id.toString()).set(
            mapOf(
                "id"             to habit.id,
                "name"           to habit.name,
                "emoji"          to habit.emoji,
                "streak"         to habit.streak,
                "completedToday" to habit.completedToday,
                "category"       to habit.category
            )
        ).await()
    }

    suspend fun resetDailyProgress() {
        val snapshot = habitsCollection.get().await()
        snapshot.documents.forEach { doc ->
            doc.reference.update("completedToday", false).await()
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut() = auth.signOut()

    // Returns today's date as a string e.g. "2026-05-05"
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}