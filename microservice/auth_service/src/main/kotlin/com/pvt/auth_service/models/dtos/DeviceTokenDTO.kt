package com.pvt.auth_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class DeviceTokenDTO(
    @JsonProperty("fcmtoken") var fcmtoken: String?,
    @JsonProperty("pushkitToken") var pushkitToken: String?,
    @JsonProperty("userID") var userID: UUID
)
