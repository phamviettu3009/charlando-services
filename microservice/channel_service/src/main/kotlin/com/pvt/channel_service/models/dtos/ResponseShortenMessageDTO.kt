package com.pvt.channel_service.models.dtos

import java.util.*

data class ResponseShortenMessageDTO(
    var id: UUID,
    var type: Int,
    var recordStatus: String?,
    var message: String?,
    var subMessage: String?,
    var iconMessage: String?,
    var makerDate: Date,
    var timeOfMessageSentDisplay: String,
    var channelID: UUID
)
