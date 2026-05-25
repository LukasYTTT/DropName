package com.lukasyt.dropname

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.nfc.NdefHelper
import com.lukasyt.dropname.theme.DropNameTheme
import com.google.gson.Gson
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val gson = Gson()

    // State that the composable can observe
    val receivedProfileJson = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if launched from NFC intent or Deep Link
        handleIncomingIntent(intent)

        setContent {
            DropNameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(receivedProfileJson = receivedProfileJson.value)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: ${intent.action}")
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val action = intent.action
        val data = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            Log.d(TAG, "Received deep link intent: $data")
            val pathSegments = data.pathSegments
            if (pathSegments.size >= 2 && pathSegments[0] == "p") {
                val profileId = pathSegments[1]
                fetchProfileFromServer(profileId)
                return
            }
        }

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == action
        ) {
            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMessages != null && rawMessages.isNotEmpty()) {
                val ndefMessage = rawMessages[0] as NdefMessage
                val profile = NdefHelper.parseProfileFromNdefMessage(ndefMessage)
                if (profile != null) {
                    Log.d(TAG, "Received profile via NFC: ${profile.name}")
                    receivedProfileJson.value = gson.toJson(profile)
                }
            }
        }
    }

    private fun fetchProfileFromServer(profileId: String) {
        lifecycleScope.launch {
            try {
                val repository = ProfileRepository(this@MainActivity)
                val profile = repository.fetchProfile(profileId)
                if (profile != null) {
                    Log.d(TAG, "Successfully loaded profile from server: ${profile.name}")
                    receivedProfileJson.value = gson.toJson(profile)
                } else {
                    Toast.makeText(this@MainActivity, "Profile could not be loaded from server", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load profile", e)
                Toast.makeText(this@MainActivity, "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
