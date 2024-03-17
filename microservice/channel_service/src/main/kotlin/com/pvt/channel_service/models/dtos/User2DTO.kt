package com.pvt.channel_service.models.dtos

import java.util.UUID

data class User2DTO(
    var id: UUID,
    var fullName: String?,
    var avatar: String?,
    var online: Boolean
)
