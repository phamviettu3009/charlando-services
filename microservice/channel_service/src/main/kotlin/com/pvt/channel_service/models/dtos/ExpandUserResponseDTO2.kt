package com.pvt.channel_service.models.dtos

import java.util.*

data class ExpandUserResponseDTO2(
    var id: UUID,
    var fullName: String?,
    var avatar: String?,
    var online: Boolean,
    var relationshipStatus: String?,
    var friend: Int,
    var channelID: UUID?,
    var coverPhoto: String?,
    var gender: String?,
    var dob: Date?,
    var phone: String?,
    var email: String?,
    var countryCode: String?,
    var languageCode: String?,
    var description: String?,
)
