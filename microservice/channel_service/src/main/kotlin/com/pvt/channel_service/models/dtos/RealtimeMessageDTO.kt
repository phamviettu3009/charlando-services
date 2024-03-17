package com.pvt.channel_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class RealtimeMessageDTO(
    @JsonProperty("message") var message: Any,
    @JsonProperty("endpoint") var endpoint: String,
    @JsonProperty("receiverID") var receiverID: UUID
)
