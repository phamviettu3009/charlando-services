package com.pvt.channel_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class OnlineStatusDTO(
    @JsonProperty("userID") val userID: UUID,
    @JsonProperty("online") val online: Boolean
)
