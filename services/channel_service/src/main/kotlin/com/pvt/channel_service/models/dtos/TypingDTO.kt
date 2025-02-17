package com.pvt.channel_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TypingDTO(
    @JsonProperty("channelID") val channelID: UUID,
    @JsonProperty("typing") val typing: Boolean
)