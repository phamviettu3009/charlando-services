package com.pvt.realtime_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class DeviceFirebaseTokenDTO(
    @JsonProperty("fcmtoken") var fcmtoken: String?,
    @JsonProperty("userID") var userID: UUID
)
