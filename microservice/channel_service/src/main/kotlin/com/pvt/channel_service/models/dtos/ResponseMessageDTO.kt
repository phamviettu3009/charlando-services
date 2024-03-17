package com.pvt.channel_service.models.dtos

import java.util.*

data class ResponseMessageDTO(
    var id: UUID,
    var type: Int,
    var message: String?,
    var subMessage: String?,
    var iconMessage: String?,
    var messageOnRightSide: Boolean,
    var consecutiveMessages: Boolean,
    var timeOfMessageSentDisplay: String,
    var edited: Boolean,
    var makerDate: Date?,
    var recordStatus: String?,
    val channelID: UUID,
    var reply: ReplyDTO?,
    var attachments: List<AttachmentDTO>?,
    var messageReactions: List<MessageReactionDTO>?,
    val user: User2DTO?,
    val sync: Boolean,
    val syncID: UUID?
)
