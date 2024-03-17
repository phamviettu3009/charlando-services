package com.pvt.channel_service.models.dtos

import java.util.UUID

data class UserInfoResponseDTO(
    var id: UUID,
    var fullName: String?,
    var avatar: String?,
    var online: Boolean,
    var relationshipStatus: String?,
    var friend: Int,
    var channelID: UUID?
)
