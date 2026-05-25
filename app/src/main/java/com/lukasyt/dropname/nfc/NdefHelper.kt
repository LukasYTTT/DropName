package com.lukasyt.dropname.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import com.google.gson.Gson
import java.nio.charset.Charset

object NdefHelper {
    private val gson = Gson()

    fun createProfileNdefMessage(profile: UserProfile): NdefMessage {
        val url = if (profile.id != null) {
            "${ProfileRepository.BASE_URL}/p/${profile.id}"
        } else {
            ProfileRepository.BASE_URL
        }
        
        val uriRecord = NdefRecord.createUri(url)
        val aar = NdefRecord.createApplicationRecord("com.lukasyt.dropname")
        
        return NdefMessage(arrayOf(uriRecord, aar))
    }

    fun parseProfileFromNdefMessage(message: NdefMessage): UserProfile? {
        for (record in message.records) {
            if (record.tnf == NdefRecord.TNF_EXTERNAL_TYPE) {
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
