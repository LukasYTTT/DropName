package com.lukasyt.dropname

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.lukasyt.dropname.data.UserProfile
import com.lukasyt.dropname.nfc.NdefHelper
import com.lukasyt.dropname.theme.DropNameTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val gson = Gson()

    // State that the composable can observe
    val receivedProfileJson = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if launched from NFC intent
        handleNfcIntent(intent)

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
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action
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
}
