package com.pvt.realtime_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class NotificationMessageDTO(
    @JsonProperty("message") var message: Map<String, Any>,
    @JsonProperty("receiverIDs") var receiverIDs: List<UUID>
)
