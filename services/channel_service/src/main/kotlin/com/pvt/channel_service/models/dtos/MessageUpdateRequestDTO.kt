package com.pvt.channel_service.models.dtos

import java.util.*

data class MessageUpdateRequestDTO(
    var message: String?,
    var messageLocation: LocationDTO?,
    var deviceLocalTime: Date?
)
