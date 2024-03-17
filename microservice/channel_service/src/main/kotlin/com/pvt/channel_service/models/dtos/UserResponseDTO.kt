package com.pvt.channel_service.models.dtos

import java.util.UUID

data class UserResponseDTO(
    var id: UUID,
    var fullName: String?,
    var avatar: String?,
    var online: Boolean
)
