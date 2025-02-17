package com.pvt.channel_service.services

import com.pvt.channel_service.models.dtos.MessageReactionRequestDTO
import com.pvt.channel_service.models.dtos.RequestDTO
import com.pvt.channel_service.models.dtos.ResponseMessageDTO
import com.pvt.channel_service.models.entitys.MessageReactionEntity
import java.util.*

interface MessageReactionService {
    fun createMessageReaction(request: RequestDTO<MessageReactionRequestDTO>): ResponseMessageDTO
    fun createMessageReaction(icon: String, userID: UUID, messageID: UUID): MessageReactionEntity?
}