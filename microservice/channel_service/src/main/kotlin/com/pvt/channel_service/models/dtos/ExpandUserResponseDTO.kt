package com.pvt.channel_service.models.dtos

import java.util.*

data class ExpandUserResponseDTO(
    var id: UUID,
    var fullName: String?,
    var avatar: String?,
    var online: Boolean,
    var coverPhoto: String?,
    var gender: String?,
    var dob: Date?,
    var phone: String?,
    var email: String?,
    var description: String?,
    val countryCode: String?,
    val languageCode: String?,
    var setting: SettingDTO?
)
