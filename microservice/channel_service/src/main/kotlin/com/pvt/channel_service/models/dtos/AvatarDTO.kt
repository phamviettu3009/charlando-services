package com.pvt.channel_service.models.dtos

import java.util.UUID

data class AvatarDTO(
    var source: UUID,
    var userID: UUID
)
