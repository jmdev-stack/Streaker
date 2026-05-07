package com.josiah.streaker.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.josiah.streaker.R
import com.josiah.streaker.ui.components.clickableNoRipple
import com.josiah.streaker.ui.theme.AppColors
import com.josiah.streaker.viewmodel.HabitViewModel

@Composable
fun SignInScreen(
    viewModel: HabitViewModel,
    onSignIn:  () -> Unit
) {
    val context   = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg  by remember { mutableStateOf("") }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    isLoading = true
                    viewModel.signInWithGoogle(
                        idToken   = idToken,
                        onSuccess = { onSignIn() },
                        onError   = { error ->
                            isLoading = false
                            errorMsg  = error
                        }
                    )
                }
            } catch (e: ApiException) {
                errorMsg = "Sign in failed: ${e.message}"
            }
        }
    }

    Box(
        modifier         = Modifier
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
            Spacer(Modifier.height(60.dp))

            // Logo + Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔥", fontSize = 72.sp)
                Spacer(Modifier.height(20.dp))
                Text(
                    "Streaker",
                    fontSize   = 40.sp,
                    fontWeight = FontWeight.Black,
                    color      = AppColors.TextPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Build habits.\nKeep your streak alive.",
                    fontSize  = 16.sp,
                    color     = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Features
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier            = Modifier.fillMaxWidth()
            ) {
                FeatureRow("🔥", "Track daily habits & streaks")
                FeatureRow("🏆", "Celebrate milestones")
                FeatureRow("☁️", "Sync across all your devices")
                FeatureRow("📊", "Monitor your progress")
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = AppColors.Destructive, fontSize = 13.sp, textAlign = TextAlign.Center)
                }

                if (isLoading) {
                    CircularProgressIndicator(color = AppColors.Ember)
                } else {
                    // Google Sign In Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickableNoRipple {
                                launcher.launch(googleSignInClient.signInIntent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("G", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF4285F4))
                            Text(
                                "Continue with Google",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = Color(0xFF1F1F1F)
                            )
                        }
                    }

                    // Continue without account
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(.15f), RoundedCornerShape(16.dp))
                            .clickableNoRipple { onSignIn() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Continue without account",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color      = AppColors.TextSecondary
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Your data is private and secure",
                    fontSize = 12.sp,
                    color    = AppColors.TextSecondary
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FeatureRow(emoji: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Surface1)
            .border(1.dp, Color.White.copy(.06f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(emoji, fontSize = 22.sp)
        Text(text, fontSize = 14.sp, color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
    }
}