package com.pvt.channel_service.services

import com.pvt.channel_service.constants.RabbitMQ
import com.pvt.channel_service.constants.RealtimeEndpoint
import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.MessageEntity
import com.pvt.channel_service.models.entitys.MessageReactionEntity
import com.pvt.channel_service.publisher.RabbitMQProducer
import com.pvt.channel_service.repositories.MessageReactionRepository
import com.pvt.channel_service.repositories.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class MessageReactionServiceImpl: MessageReactionService {
    @Autowired
    private lateinit var messageReactionRepository: MessageReactionRepository

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    @Autowired
    private  lateinit var messageService: MessageService

    private fun sendRealtimeMessageReaction(userIDs: List<UUID>, messageID: UUID, endpoint: String) {
        for (userID in userIDs) {
            val message = messageService.getMessage(messageID, userID)
            val realtimeMessage = RealtimeMessageDTO(message, endpoint, userID)
            rabbitMQProducer.sendMessage(realtimeMessage, RabbitMQ.MSCMN_SEND_REALTIME_MESSAGE.route())
        }
    }

    @Transactional
    override fun createMessageReaction(icon: String, userID: UUID, messageID: UUID): MessageReactionEntity? {
        val reaction = messageReactionRepository.findByMessageIDAndIconAndMakerIDAndAuthStatus(
            messageID = messageID,
            icon = icon,
            makerID = userID
        ).orElse(null)

        if (reaction == null) {
            val newRecord = MessageReactionEntity(
                messageID = messageID,
                icon = icon,
                makerID = userID
            )
            return messageReactionRepository.saveAndFlush(newRecord)
        }
        messageReactionRepository.delete(reaction)
        return null
    }

    @Transactional
    override fun createMessageReaction(request: RequestDTO<MessageReactionRequestDTO>): ResponseMessageDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val messageID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val icon = request.payload?.icon ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val messageRecord = messageService.getMessage(messageID, ownerID)
        memberService.findByChannelIDAndUserID(messageRecord.channelID,  ownerID)
        val membersInChannel = memberService.findAllByChannelID(messageRecord.channelID)
        val userIDs = membersInChannel.map { it.userID }.filter { it != ownerID }
        createMessageReaction(icon, ownerID, messageID)
        val message = messageService.getMessage(messageID, ownerID)
        val endpoint = RealtimeEndpoint.MESSAGE_REACTION_BY_CHANNEL + messageRecord.channelID
        sendRealtimeMessageReaction(userIDs, messageID, endpoint)
        return message
    }
}