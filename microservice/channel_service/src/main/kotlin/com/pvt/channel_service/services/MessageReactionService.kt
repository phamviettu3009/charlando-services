package com.pvt.channel_service.services

import com.pvt.channel_service.models.dtos.MessageReactionRequestDTO
import com.pvt.channel_service.models.dtos.RequestDTO
import com.pvt.channel_service.models.entitys.MessageReactionEntity
import com.pvt.channel_service.repositories.MessageReactionRepository
import java.util.*

interface MessageReactionService {
    fun createMessageReaction(request: RequestDTO<MessageReactionRequestDTO>): Any
    fun createMessageReaction(icon: String, userID: UUID, messageID: UUID): MessageReactionEntity?
    fun getMessageReactions(messageIDs: List<UUID>, userID: UUID): List<MessageReactionRepository.MultiCountReaction>
}