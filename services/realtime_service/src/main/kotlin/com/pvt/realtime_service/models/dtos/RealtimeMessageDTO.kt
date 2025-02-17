package com.pvt.realtime_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class RealtimeMessageDTO(
    @JsonProperty("message") var message: Any,
    @JsonProperty("endpoint") var endpoint: String,
    @JsonProperty("receiverID") var receiverID: UUID
)
