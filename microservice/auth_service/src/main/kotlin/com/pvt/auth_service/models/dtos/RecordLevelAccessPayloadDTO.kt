package com.pvt.auth_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.pvt.auth_service.models.entitys.RecordLevelAccessEntity
import com.pvt.auth_service.models.entitys.UsersWithRecordLevelAccessEntity
import java.util.*

data class RecordLevelAccessPayloadDTO(
    @JsonProperty("accessContent") var accessContent: String,
    @JsonProperty("method") var method: String,
    @JsonProperty("recordStatus") var recordStatus: String,
    @JsonProperty("ownerID") var ownerID: UUID?,
    @JsonProperty("userAccessIDs") var userAccessIDs: List<UUID>
)

fun RecordLevelAccessPayloadDTO.asRecordLevelAccessEntity(): RecordLevelAccessEntity {
    return RecordLevelAccessEntity(
        accessContent = accessContent,
        method = method,
        recordStatus = recordStatus,
    )
}

fun RecordLevelAccessPayloadDTO.asListUsersWithRecordLevelAccessEntity(
    recordLevelAccessID: UUID
): List<UsersWithRecordLevelAccessEntity> {
    val hasID = mutableMapOf<UUID, Any>()
    val listResult = mutableListOf<UsersWithRecordLevelAccessEntity>()
    for (userAccessID in userAccessIDs) {
        if (hasID[userAccessID] == null && userAccessID != ownerID) {
            listResult.add(UsersWithRecordLevelAccessEntity(recordLevelAccessID = recordLevelAccessID,  userID = userAccessID))
            hasID[userAccessID] = true
        }
    }

    if (ownerID != null) {
        val ownerRecord = UsersWithRecordLevelAccessEntity(recordLevelAccessID = recordLevelAccessID, userID = ownerID!!)
        return listOf(ownerRecord) + listResult
    }

    return listResult
}
