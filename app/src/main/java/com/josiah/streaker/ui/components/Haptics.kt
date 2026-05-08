package com.josiah.streaker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun rememberHaptic(): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return remember {
        { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
    }
}

@Composable
fun rememberLightHaptic(): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return remember {
        { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    }
}