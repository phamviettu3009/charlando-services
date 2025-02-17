package com.pvt.resource_service.models.dtos

import java.util.UUID

data class RecordLevelAccessPayloadDTO(
    var accessContent: String,
    var method: String,
    var recordStatus: String,
    var ownerID: UUID?,
    var userAccessIDs: List<UUID>
)
