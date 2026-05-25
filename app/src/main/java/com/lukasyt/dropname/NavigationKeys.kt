package com.lukasyt.dropname

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Splash : NavKey
@Serializable data object Login : NavKey
@Serializable data object Onboarding : NavKey
@Serializable data object ProfileSetup : NavKey
@Serializable data object Home : NavKey
@Serializable data object History : NavKey
@Serializable data class QRCode(val profileJson: String) : NavKey
@Serializable data class ReceivedProfile(val profileJson: String) : NavKey
