package com.pvt.channel_service.models.dtos

import java.util.*

data class MessageRequestDTO(
    var message: String?,
    var messageLocation: LocationDTO?,
    var deviceLocalTime: Date?,
    var iconMessage: String?,
    var replyID: String?,
    var attachmentIDs: List<String>?,
    var messageReaction: String?,
    var syncID: String
)
