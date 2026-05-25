package com.lukasyt.dropname.data

data class ProfileField(
    val label: String,
    val value: String
)

data class UserProfile(
    val name: String,
    val fields: List<ProfileField> = emptyList(),
    val profileImageBase64: String? = null, // Compressed thumbnail for NFC
    val id: String? = null
)
