package com.pvt.auth_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class DeviceFirebaseTokenDTO(
    @JsonProperty("fcmtoken") var fcmtoken: String?,
    @JsonProperty("userID") var userID: UUID
)
