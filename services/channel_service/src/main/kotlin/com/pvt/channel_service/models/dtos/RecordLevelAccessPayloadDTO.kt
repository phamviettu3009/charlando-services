package com.pvt.channel_service.models.dtos

import java.util.*

data class RecordLevelAccessPayloadDTO(
    var accessContent: String,
    var method: String,
    var recordStatus: String,
    var ownerID: UUID?,
    var userAccessIDs: List<UUID> = emptyList()
)
