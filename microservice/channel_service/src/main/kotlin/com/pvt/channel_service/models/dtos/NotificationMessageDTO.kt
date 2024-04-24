package com.pvt.channel_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class NotificationMessageDTO(
    @JsonProperty("message") var message: Map<String, String>,
    @JsonProperty("receiverIDs") var receiverIDs: List<UUID>
)
