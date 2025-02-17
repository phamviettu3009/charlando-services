package com.pvt.realtime_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class UserResponseDTO(
    @JsonProperty("id") var id: UUID,
    @JsonProperty("fullName") var fullName: String?,
    @JsonProperty("avatar") var avatar: String?,
    @JsonProperty("online") var online: Boolean
)
