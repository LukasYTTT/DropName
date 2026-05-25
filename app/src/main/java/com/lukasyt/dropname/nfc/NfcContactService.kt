package com.lukasyt.dropname.nfc

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.lukasyt.dropname.data.ProfileRepository
import com.lukasyt.dropname.data.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NfcContactService : HostApduService() {
    private val TAG = "NfcContactService"
    
    companion object {
        private val APDU_SELECT = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(), 
            0x07.toByte(), 0xD2.toByte(), 0x76.toByte(), 0x00.toByte(), 
            0x00.toByte(), 0x85.toByte(), 0x01.toByte(), 0x01.toByte(), 
            0x00.toByte()
        )

        private val CAPABILITY_CONTAINER_OK = byteArrayOf(
            0x00.toByte(), 0x0F.toByte(), 0x20.toByte(), 0x00.toByte(), 
            0x3B.toByte(), 0x00.toByte(), 0x34.toByte(), 0x04.toByte(), 
            0x06.toByte(), 0xE1.toByte(), 0x04.toByte(), 0x0F.toByte(), 
            0xFF.toByte(), 0x00.toByte(), 0xFF.toByte(), 0x90.toByte(), 
            0x00.toByte()
        )

        private val READ_CAPABILITY_CONTAINER = byteArrayOf(
            0x00.toByte(), 0xb0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x0f.toByte()
        )

        private val READ_CAPABILITY_CONTAINER_CHECK = byteArrayOf(
            0x00.toByte(), 0xb0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x11.toByte()
        )

        private val READ_CAPABILITY_CONTAINER_RESPONSE = byteArrayOf(
            0x00.toByte(), 0x11.toByte(), 0x20.toByte(), 0xFF.toByte(),
            0xFF.toByte(), 0x00.toByte(), 0xFF.toByte(), 0x04.toByte(),
            0x06.toByte(), 0xE1.toByte(), 0x04.toByte(), 0x0F.toByte(),
            0xFF.toByte(), 0x00.toByte(), 0xFF.toByte(), 0x90.toByte(),
            0x00.toByte()
        )

        private val NDEF_SELECT_OK = byteArrayOf(0x90.toByte(), 0x00.toByte())
        
        private val NDEF_READ_BINARY = byteArrayOf(
            0x00.toByte(), 0xb0.toByte()
        )

        private val NDEF_SELECT = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x00.toByte(), 0x0C.toByte(), 0x02.toByte(), 0xE1.toByte(), 0x04.toByte()
        )
    }

    private var profileRepository: ProfileRepository? = null
    private var ndefData: ByteArray? = null
    private var serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()
        profileRepository = ProfileRepository(applicationContext)
        loadProfileData()
    }
    
    private fun loadProfileData() {
        serviceScope.launch {
            val profile = profileRepository?.userProfileFlow?.first()
            if (profile != null) {
                val ndefMessage = NdefHelper.createProfileNdefMessage(profile)
                val ndefBytes = ndefMessage.toByteArray()
                val ndefLength = ndefBytes.size
                
                // Add length prefix as per Type 4 tag spec
                ndefData = ByteArray(ndefLength + 2)
                ndefData!![0] = ((ndefLength shr 8) and 0xFF).toByte()
                ndefData!![1] = (ndefLength and 0xFF).toByte()
                System.arraycopy(ndefBytes, 0, ndefData!!, 2, ndefLength)
            }
        }
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) return NDEF_SELECT_OK

        // 1. SELECT AID
        if (commandApdu.contentEquals(APDU_SELECT)) {
            Log.d(TAG, "processCommandApdu: SELECT AID")
            return NDEF_SELECT_OK
        }
        
        // 2. SELECT CC
        if (commandApdu.contentEquals(byteArrayOf(0x00.toByte(), 0xA4.toByte(), 0x00.toByte(), 0x0C.toByte(), 0x02.toByte(), 0xE1.toByte(), 0x03.toByte()))) {
            Log.d(TAG, "processCommandApdu: SELECT CC")
            return NDEF_SELECT_OK
        }
        
        // 3. READ CC
        if (commandApdu.contentEquals(READ_CAPABILITY_CONTAINER) || commandApdu.contentEquals(READ_CAPABILITY_CONTAINER_CHECK)) {
            Log.d(TAG, "processCommandApdu: READ CC")
            return READ_CAPABILITY_CONTAINER_RESPONSE
        }
        
        // 4. SELECT NDEF
        if (commandApdu.contentEquals(NDEF_SELECT)) {
            Log.d(TAG, "processCommandApdu: SELECT NDEF")
            return NDEF_SELECT_OK
        }
        
        // 5. READ NDEF
        if (commandApdu.size >= 2 && commandApdu[0] == NDEF_READ_BINARY[0] && commandApdu[1] == NDEF_READ_BINARY[1]) {
            Log.d(TAG, "processCommandApdu: READ NDEF")
            val offset = ((commandApdu[2].toInt() and 0xFF) shl 8) or (commandApdu[3].toInt() and 0xFF)
            val length = commandApdu[4].toInt() and 0xFF
            
            if (ndefData == null) {
                return NDEF_SELECT_OK // Empty or error
            }

            val data = ndefData!!
            val responseLength = if (offset + length <= data.size) length else data.size - offset
            
            if (responseLength <= 0) {
                return NDEF_SELECT_OK
            }

            val response = ByteArray(responseLength + 2)
            System.arraycopy(data, offset, response, 0, responseLength)
            response[responseLength] = 0x90.toByte()
            response[responseLength + 1] = 0x00.toByte()
            return response
        }

        return NDEF_SELECT_OK
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
