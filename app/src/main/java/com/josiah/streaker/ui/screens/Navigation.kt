package com.josiah.streaker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.josiah.streaker.viewmodel.HabitViewModel

@Composable
fun AppNavigation(viewModel: HabitViewModel) {
    val navController     = rememberNavController()
    val isSignedIn        by viewModel.isSignedIn.collectAsStateWithLifecycle()
    val hasSeenOnboarding = viewModel.hasSeenOnboarding()

    val startDestination = when {
        Firebase.auth.currentUser != null -> "home"
        hasSeenOnboarding                 -> "signin"
        else                              -> "onboarding"
    }

    NavHost(
        navController      = navController,
        startDestination   = startDestination,
        enterTransition    = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec  = tween(300)
            ) + fadeIn(tween(300))
        },
        exitTransition     = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeOut(tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec  = tween(300)
            ) + fadeIn(tween(300))
        },
        popExitTransition  = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut(tween(300))
        }
    ) {
        composable("signin") {
            SignInScreen(
                viewModel = viewModel,
                onSignIn  = {
                    navController.navigate("home") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    viewModel.markOnboardingSeen()
                    navController.navigate("signin") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                viewModel       = viewModel,
                onAddClick      = { navController.navigate("add") },
                onHabitClick    = { habit -> navController.navigate("detail/${habit.id}") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("add") {
            AddHabitScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }
        composable(
            route     = "detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            HabitDetailScreen(
                habitId   = habitId,
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack    = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }

    // Listen for sign out and redirect to signin
    if (!isSignedIn && Firebase.auth.currentUser == null) {
        val current = navController.currentDestination?.route
        if (current != "signin" && current != "onboarding") {
            navController.navigate("signin") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}