package com.josiah.streaker.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.josiah.streaker.HabitRepository
import com.josiah.streaker.ThemePreference
import com.josiah.streaker.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository     = HabitRepository()
    private val auth           = Firebase.auth
    private val themePreference = ThemePreference(application)

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits

    private val _celebrationStreak = MutableStateFlow<Int?>(null)
    val celebrationStreak: StateFlow<Int?> = _celebrationStreak

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSignedIn = MutableStateFlow(auth.currentUser != null)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    val isDarkTheme: StateFlow<Boolean> = themePreference.isDarkTheme
        .stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = true
        )

    private val milestones = setOf(7, 14, 30, 60, 100, 365)

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            repository.getHabits().collect { habits ->
                _habits.value    = habits
                _isLoading.value = false
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            themePreference.setDarkTheme(!isDarkTheme.value)
        }
    }

    fun checkAndResetDaily() {
        val prefs     = getApplication<Application>().getSharedPreferences("streaker_prefs", Context.MODE_PRIVATE)
        val lastReset = prefs.getString("last_reset_date", "")
        val today     = repository.getTodayDate()
        if (lastReset != today) {
            viewModelScope.launch {
                repository.resetDailyProgress()
                prefs.edit().putString("last_reset_date", today).apply()
            }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _isSignedIn.value = true
                _isLoading.value  = true
                loadHabits()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Sign in failed")
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        repository.signOut()
        _habits.value     = emptyList()
        _isSignedIn.value = false
        onComplete()
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.addHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun completeHabit(habit: Habit) {
        val newStreak = habit.streak + 1
        viewModelScope.launch {
            repository.updateHabit(
                habit.copy(streak = newStreak, completedToday = true)
            )
        }
        if (newStreak in milestones) {
            _celebrationStreak.value = newStreak
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun dismissCelebration() {
        _celebrationStreak.value = null
    }

    fun getCurrentUser() = repository.getCurrentUser()

    fun hasSeenOnboarding(): Boolean {
        val prefs = getApplication<Application>().getSharedPreferences("streaker_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("has_seen_onboarding", false)
    }

    fun markOnboardingSeen() {
        val prefs = getApplication<Application>().getSharedPreferences("streaker_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_seen_onboarding", true).apply()
    }
}