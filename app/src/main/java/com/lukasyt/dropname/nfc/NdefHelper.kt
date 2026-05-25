package com.lukasyt.dropname.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import com.lukasyt.dropname.data.UserProfile
import com.google.gson.Gson
import java.nio.charset.Charset

object NdefHelper {
    private val gson = Gson()

    fun createProfileNdefMessage(profile: UserProfile): NdefMessage {
        val json = gson.toJson(profile)
        val payload = json.toByteArray(Charset.forName("UTF-8"))
        
        // Use an external type record
        val domain = "com.lukasyt.dropname"
        val type = "profile"
        val extRecord = NdefRecord.createExternal(domain, type, payload)
        
        // Include AAR to ensure the app is opened if the other device just taps it normally
        val aar = NdefRecord.createApplicationRecord("com.lukasyt.dropname")
        
        return NdefMessage(arrayOf(extRecord, aar))
    }

    fun parseProfileFromNdefMessage(message: NdefMessage): UserProfile? {
        for (record in message.records) {
            if (record.tnf == NdefRecord.TNF_EXTERNAL_TYPE) {
                // Parse the domain and type to verify
                val typeStr = String(record.type, Charset.forName("US-ASCII"))
                if (typeStr == "com.lukasyt.dropname:profile") {
                    try {
                        val json = String(record.payload, Charset.forName("UTF-8"))
                        return gson.fromJson(json, UserProfile::class.java)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return null
    }
}
