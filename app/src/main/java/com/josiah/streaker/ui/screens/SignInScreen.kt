package com.josiah.streaker.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun SignInScreen(
    viewModel: HabitViewModel,
    onSignIn:  () -> Unit
) {
    var isLoginMode     by remember { mutableStateOf(true) }
    var email           by remember { mutableStateOf("") }
    var username        by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }
    var errorMsg        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

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
                errorMsg = "Google sign in failed"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Logo
            Text("🔥", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "Streaker",
                fontSize   = 36.sp,
                fontWeight = FontWeight.Black,
                color      = AppColors.TextPrimary
            )
            Text(
                "Build habits. Keep your streak alive.",
                fontSize  = 14.sp,
                color     = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Tab switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Surface1)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isLoginMode)
                                Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold))
                            else
                                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickableNoRipple { isLoginMode = true; errorMsg = "" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sign In",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = if (isLoginMode) Color.White else AppColors.TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (!isLoginMode)
                                Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold))
                            else
                                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickableNoRipple { isLoginMode = false; errorMsg = "" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sign Up",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = if (!isLoginMode) Color.White else AppColors.TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Form fields
            if (!isLoginMode) {
                // Username field (sign up only)
                OutlinedTextField(
                    value         = username,
                    onValueChange = { username = it; errorMsg = "" },
                    placeholder   = { Text("Username", color = AppColors.TextSecondary) },
                    leadingIcon   = {
                        Icon(Icons.Default.Person, null, tint = AppColors.TextSecondary)
                    },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(14.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = AppColors.Ember,
                        unfocusedBorderColor    = Color.White.copy(.1f),
                        focusedTextColor        = AppColors.TextPrimary,
                        unfocusedTextColor      = AppColors.TextPrimary,
                        cursorColor             = AppColors.Ember,
                        focusedContainerColor   = AppColors.Surface1,
                        unfocusedContainerColor = AppColors.Surface1,
                    )
                )
                Spacer(Modifier.height(12.dp))
            }

            // Email field
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it; errorMsg = "" },
                placeholder   = { Text("Email", color = AppColors.TextSecondary) },
                leadingIcon   = {
                    Icon(Icons.Default.Email, null, tint = AppColors.TextSecondary)
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = AppColors.Ember,
                    unfocusedBorderColor    = Color.White.copy(.1f),
                    focusedTextColor        = AppColors.TextPrimary,
                    unfocusedTextColor      = AppColors.TextPrimary,
                    cursorColor             = AppColors.Ember,
                    focusedContainerColor   = AppColors.Surface1,
                    unfocusedContainerColor = AppColors.Surface1,
                )
            )

            Spacer(Modifier.height(12.dp))

            // Password field
            OutlinedTextField(
                value                  = password,
                onValueChange          = { password = it; errorMsg = "" },
                placeholder            = { Text("Password", color = AppColors.TextSecondary) },
                leadingIcon            = {
                    Icon(Icons.Default.Lock, null, tint = AppColors.TextSecondary)
                },
                trailingIcon           = {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        null,
                        tint     = AppColors.TextSecondary,
                        modifier = Modifier.clickableNoRipple {
                            passwordVisible = !passwordVisible
                        }
                    )
                },
                visualTransformation   = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine             = true,
                modifier               = Modifier.fillMaxWidth(),
                shape                  = RoundedCornerShape(14.dp),
                colors                 = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = AppColors.Ember,
                    unfocusedBorderColor    = Color.White.copy(.1f),
                    focusedTextColor        = AppColors.TextPrimary,
                    unfocusedTextColor      = AppColors.TextPrimary,
                    cursorColor             = AppColors.Ember,
                    focusedContainerColor   = AppColors.Surface1,
                    unfocusedContainerColor = AppColors.Surface1,
                )
            )

            if (!isLoginMode) {
                Spacer(Modifier.height(12.dp))
                // Confirm password field
                OutlinedTextField(
                    value                = confirmPassword,
                    onValueChange        = { confirmPassword = it; errorMsg = "" },
                    placeholder          = { Text("Confirm Password", color = AppColors.TextSecondary) },
                    leadingIcon          = {
                        Icon(Icons.Default.Lock, null, tint = AppColors.TextSecondary)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine           = true,
                    modifier             = Modifier.fillMaxWidth(),
                    shape                = RoundedCornerShape(14.dp),
                    colors               = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = AppColors.Ember,
                        unfocusedBorderColor    = Color.White.copy(.1f),
                        focusedTextColor        = AppColors.TextPrimary,
                        unfocusedTextColor      = AppColors.TextPrimary,
                        cursorColor             = AppColors.Ember,
                        focusedContainerColor   = AppColors.Surface1,
                        unfocusedContainerColor = AppColors.Surface1,
                    )
                )
            }

            // Error message
            if (errorMsg.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    errorMsg,
                    color     = AppColors.Destructive,
                    fontSize  = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            // Main action button
            if (isLoading) {
                CircularProgressIndicator(color = AppColors.Ember)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(AppColors.Ember, AppColors.Gold))
                        )
                        .clickableNoRipple {
                            errorMsg = ""
                            if (email.isBlank() || password.isBlank()) {
                                errorMsg = "Please fill in all fields"
                                return@clickableNoRipple
                            }
                            if (!isLoginMode) {
                                if (username.isBlank()) {
                                    errorMsg = "Please enter a username"
                                    return@clickableNoRipple
                                }
                                if (password != confirmPassword) {
                                    errorMsg = "Passwords do not match"
                                    return@clickableNoRipple
                                }
                                if (password.length < 6) {
                                    errorMsg = "Password must be at least 6 characters"
                                    return@clickableNoRipple
                                }
                                isLoading = true
                                viewModel.signUpWithEmail(
                                    email       = email.trim(),
                                    password    = password,
                                    username    = username.trim(),
                                    onSuccess   = { onSignIn() },
                                    onError     = { error ->
                                        isLoading = false
                                        errorMsg  = error
                                    }
                                )
                            } else {
                                isLoading = true
                                viewModel.signInWithEmail(
                                    email     = email.trim(),
                                    password  = password,
                                    onSuccess = { onSignIn() },
                                    onError   = { error ->
                                        isLoading = false
                                        errorMsg  = error
                                    }
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isLoginMode) "Sign In 🔥" else "Create Account 🔥",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.White.copy(.1f))
                    )
                    Text(
                        "  or  ",
                        fontSize = 12.sp,
                        color    = AppColors.TextSecondary
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.White.copy(.1f))
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Google Sign In
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

                Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(24.dp))
            Text(
                "Your data is private and secure 🔒",
                fontSize  = 12.sp,
                color     = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}