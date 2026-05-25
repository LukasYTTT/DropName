package com.lukasyt.dropname

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.ui.screens.*
import com.google.gson.Gson

@Composable
fun MainNavigation(receivedProfileJson: String? = null) {
    val backStack = rememberNavBackStack(Splash)
    val gson = Gson()

    // If a profile was received via NFC in the background, navigate there
    LaunchedEffect(receivedProfileJson) {
        if (receivedProfileJson != null) {
            backStack.add(ReceivedProfile(receivedProfileJson))
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<Splash> {
                    SplashScreen(
                        onNavigateToHome = {
                            backStack.clear()
                            backStack.add(Home)
                        },
                        onNavigateToOnboarding = {
                            backStack.clear()
                            backStack.add(Login)
                        }
                    )
                }
                entry<Login> {
                    LoginScreen(
                        onNavigateToNext = {
                            backStack.clear()
                            backStack.add(Onboarding)
                        }
                    )
                }
                entry<Onboarding> {
                    OnboardingScreen(
                        onNavigateToSetup = { backStack.add(ProfileSetup) }
                    )
                }
                entry<ProfileSetup> {
                    ProfileSetupScreen(
                        onNavigateToHome = {
                            backStack.clear()
                            backStack.add(Home)
                        }
                    )
                }
                entry<Home> {
                    HomeScreen(
                        onNavigateToHistory = { backStack.add(History) },
                        onNavigateToSetup = { backStack.add(ProfileSetup) },
                        onNavigateToQrCode = { profile -> 
                            backStack.add(QRCode(gson.toJson(profile)))
                        }
                    )
                }
                entry<History> {
                    HistoryScreen(
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onProfileClick = { profile ->
                            val json = gson.toJson(profile)
                            backStack.add(ReceivedProfile(json))
                        }
                    )
                }
                entry<QRCode> { qrRoute ->
                    val profile = gson.fromJson(qrRoute.profileJson, UserProfile::class.java)
                    QRCodeScreen(
                        profile = profile,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<ReceivedProfile> { received ->
                    val profile = gson.fromJson(received.profileJson, UserProfile::class.java)
                    ReceivedProfileScreen(
                        profile = profile,
                        onClose = {
                            backStack.clear()
                            backStack.add(Home)
                        }
                    )
                }
            },
    )
}
