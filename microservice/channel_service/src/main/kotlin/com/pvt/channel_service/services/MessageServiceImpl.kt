package com.pvt.channel_service.services

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.constants.Message
import com.pvt.channel_service.constants.RabbitMQ
import com.pvt.channel_service.constants.RealtimeEndpoint
import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.MemberEntity
import com.pvt.channel_service.models.entitys.MessageEntity
import com.pvt.channel_service.models.entitys.MessageReadersEntity
import com.pvt.channel_service.publisher.RabbitMQProducer
import com.pvt.channel_service.repositories.ChannelRepository
import com.pvt.channel_service.repositories.MemberRepository
import com.pvt.channel_service.repositories.MessageReadersRepository
import com.pvt.channel_service.repositories.MessageRepository
import com.pvt.channel_service.utils.MessageModifier
import com.pvt.channel_service.utils.converter.DateTimeConverter
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Date
import java.util.UUID

@Service
class MessageServiceImpl : MessageService {
    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var attachmentService: AttachmentService

    @Autowired
    private lateinit var messageReactionService: MessageReactionService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var channelService: ChannelService

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    @Autowired
    private lateinit var messageReadersRepository: MessageReadersRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    private fun putMessageCreateRecordLevelAccesses(payloads: List<RecordLevelAccessPayloadDTO>) {
        rabbitMQProducer.sendAndCallbackMessage<String>(
            payloads,
            RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.route(),
            RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackQueue()
        )
    }

    private fun createRecordLevelAccessForMembers(messageID: UUID, ownerID: UUID, userAccessIDs: List<UUID>) {
        val recordLevelAccessUpdateMessage = RecordLevelAccessPayloadDTO(
            accessContent = "/message/${messageID}",
            method = "PUT",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = listOf()
        )

        val recordLevelAccessSendReaction = RecordLevelAccessPayloadDTO(
            accessContent = "/message/${messageID}/reaction",
            method = "POST",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = userAccessIDs
        )

        val recordLevelAccessDeleteMessageForAll = RecordLevelAccessPayloadDTO(
            accessContent = "/message/${messageID}/for-all",
            method = "DELETE",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = listOf()
        )

        val recordLevelAccessDeleteMessageForOwner = RecordLevelAccessPayloadDTO(
            accessContent = "/message/${messageID}/for-owner",
            method = "DELETE",
            recordStatus = "private",
            ownerID = ownerID,
            userAccessIDs = listOf()
        )

        putMessageCreateRecordLevelAccesses(listOf(
            recordLevelAccessUpdateMessage,
            recordLevelAccessSendReaction,
            recordLevelAccessDeleteMessageForAll,
            recordLevelAccessDeleteMessageForOwner
        ))
    }

    private fun createRecordLevelAccessAttachmentForMembers(attachmentIDs: List<String>?, userAccessIDs: List<UUID>) {
        if (attachmentIDs == null) return
        val payloads = attachmentIDs.map {
            RecordLevelAccessPayloadDTO(
                accessContent = "/resource/get/${it}",
                method = "GET",
                recordStatus = "private",
                ownerID = null,
                userAccessIDs = userAccessIDs
            )
        }
        rabbitMQProducer.sendAndCallbackMessage<String>(
            payloads,
            RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.route(),
            RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackQueue()
        )
    }

    private fun messageTypeHandler(attachmentIDs: List<String>?, iconMessage: String?): Int {
        if (iconMessage != null) return Message.Type.ICON_MESSAGE
        if (attachmentIDs != null && attachmentIDs.isNotEmpty()) return Message.Type.ATTACHMENT
        return Message.Type.MESSAGE
    }

    private fun createRecordUnreadCounter(members: List<MemberEntity>, ownerID: UUID) {
        val newRecords = members.filter { it.userID != ownerID }.map {
            it.unreadCounter += 1
            it
        }
        memberRepository.saveAllAndFlush(newRecords)
    }

    private fun convertResponseMessageDTO(messageEntity: MessageEntity, ownerID: UUID, syncID: UUID? = null): ResponseMessageDTO {
        val messageIDs = listOf(messageEntity.id)
        val replyIDs = if (messageEntity.replyID != null) listOf(messageEntity.replyID!!) else emptyList()
        val makerIDs = listOf(messageEntity.makerID)

        val messageModifier = getMessageModifier(messageIDs, replyIDs, makerIDs, ownerID)
        val attachmentModifier = messageModifier["attachmentModifier"] as Map<String, List<AttachmentDTO>>
        val reactionsModifier = messageModifier["reactionsModifier"] as Map<String, List<MessageReactionDTO>>
        val replyModifier = messageModifier["replyModifier"] as Map<String, ReplyDTO>
        val userModifier = messageModifier["userModifier"] as Map<String, User2DTO>

        return messageEntity.asResponseMessageDTO(attachmentModifier, reactionsModifier, replyModifier, userModifier, ownerID, syncID)
    }

    private fun sendRealtimeMessage(userIDs: List<UUID>, messageEntity: MessageEntity, endpoint: String) {
        for (userID in userIDs) {
            val payload = convertResponseMessageDTO(messageEntity, userID)
            val realtimeMessage = RealtimeMessageDTO(payload, endpoint, userID)
            rabbitMQProducer.sendMessage(realtimeMessage, RabbitMQ.MSCMN_SEND_REALTIME_MESSAGE.route())
            sendRealtimeChannel(messageEntity.channelID, userID)
        }
    }

    private fun sendRealtimeChannel(channelID: UUID, userID: UUID) {
        val channel = channelService.getChannel(channelID, userID)
        val endpoint = RealtimeEndpoint.CHANNEL_BY_USER + userID
        val realtimeMessage = RealtimeMessageDTO(channel, endpoint, userID)
        rabbitMQProducer.sendMessage(realtimeMessage, RabbitMQ.MSCMN_SEND_REALTIME_MESSAGE.route())
    }

    @Transactional
    private fun createAttachments(attachmentIDs: List<String>?, userID: UUID, messageID: UUID) {
        if (attachmentIDs == null || attachmentIDs.isEmpty()) return
        val attachmentIDsUUID = attachmentIDs.map { it.asUUID() }

        attachmentService.createAttachments(
            attachmentIDsUUID,
            userID = userID,
            messageID = messageID
        )
    }

    @Transactional
    private fun createReaction(icon: String?, userID: UUID, messageID: UUID) {
        if (icon == null) return
        messageReactionService.createMessageReaction(icon, userID, messageID)
    }

    private fun verifyReply(replyID: UUID?, channelID: UUID) {
        if (replyID == null) return
        messageRepository.findByIdAndChannelIDAndAuthStatus(replyID, channelID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }

    private fun findAllReplyByIDs(replyIDs: List<UUID>): List<MessageEntity> {
        return messageRepository.findAllByReplyIDs(replyIDs)
    }

    fun getMessageModifier(messageIDs: List<UUID>, replyIDs: List<UUID>, makerIDs: List<UUID> ,ownerID: UUID): Map<String, Any> {
        var replyModifier: Map<String, ReplyDTO> = mapOf()

        val attachments = attachmentService.getAttachments(messageIDs)
        val attachmentModifier = MessageModifier.getAttachmentModifier(attachments)
        val reactions = messageReactionService.getMessageReactions(messageIDs, ownerID)
        val reactionsModifier = MessageModifier.getReactionModifier(reactions)
        val users = userService.findAllByIDs(makerIDs)
        val userModifier = MessageModifier.getUserModifier(users)

        if (replyIDs.isNotEmpty()) {
            val replies = findAllReplyByIDs(replyIDs)
            val replyAttachments = attachmentService.getAttachments(replyIDs)
            val replyAttachmentModifier = MessageModifier.getAttachmentModifier(replyAttachments)
            replyModifier = MessageModifier.getReplyModifier(replies, replyAttachmentModifier)
        }

        return mapOf(
            "attachmentModifier" to attachmentModifier,
            "reactionsModifier" to reactionsModifier,
            "replyModifier" to replyModifier,
            "userModifier" to userModifier
        )
    }

    private fun getPreviousMessage(channelID: UUID): MessageEntity? {
        return messageRepository.findLastMessageByChannelID(channelID).orElse(null)
    }

    private fun getConsecutiveMessage(preMessage: MessageEntity?, ownerID: UUID, makerDate: Date): Boolean {
        if (preMessage == null) return false
        if (preMessage.makerID != ownerID) return false
        return DateTimeConverter.convertConsecutiveMessage(makerDate, preMessage.makerDate!!)
    }

    @Transactional
    override fun createMessage(request: RequestDTO<MessageRequestDTO>): ResponseMessageDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val bodyRequest = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val message = bodyRequest.message
        val replyID = if (bodyRequest.replyID != null) bodyRequest.replyID!!.asUUID() else null
        val deviceLocalTime = bodyRequest.deviceLocalTime
        val messageLocation = bodyRequest.messageLocation
        val iconMessage = bodyRequest.iconMessage
        val attachmentIDs = bodyRequest.attachmentIDs
        val messageReaction = bodyRequest.messageReaction
        val syncID = bodyRequest.syncID.asUUID()

        val channel = channelService.getChannel(channelID)
        verifyReply(replyID, channel.id)
        val preMessage = getPreviousMessage(channel.id)
        val makerDate = Date()
        val newMessageRecord = MessageEntity(
            channelID = channel.id,
            message = message,
            replyID = replyID,
            deviceLocalTime = deviceLocalTime,
            messageLocation = messageLocation,
            iconMessage = iconMessage,
            makerID = ownerID,
            makerDate = makerDate,
            consecutiveMessages = getConsecutiveMessage(preMessage, ownerID, makerDate),
            type = messageTypeHandler(attachmentIDs, iconMessage)
        )

        val messageRecord = messageRepository.saveAndFlush(newMessageRecord)
        createAttachments(attachmentIDs, ownerID, messageRecord.id)
        createReaction(messageReaction, ownerID, messageRecord.id)
        val membersInChannel = memberService.findAllByChannelID(channel.id)
        val userIDs = membersInChannel.map { it.userID }.filter { it != ownerID }
        createRecordLevelAccessForMembers(messageRecord.id, ownerID, userIDs)
        createRecordLevelAccessAttachmentForMembers(attachmentIDs, userIDs)
        createRecordUnreadCounter(membersInChannel, ownerID)
        channel.lastMessageTime = Date()
        channelRepository.saveAndFlush(channel)
        val endpoint = RealtimeEndpoint.NEW_MESSAGE_BY_CHANNEL + channelID
        sendRealtimeMessage(userIDs, messageRecord, endpoint)
        return convertResponseMessageDTO(messageRecord, ownerID, syncID)
    }

    @Transactional
    override fun updateMessage(request: RequestDTO<MessageUpdateRequestDTO>): ResponseMessageDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val messageID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val bodyRequest = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val message = bodyRequest.message
        val deviceLocalTime = bodyRequest.deviceLocalTime
        val messageLocation = bodyRequest.messageLocation

        val messageRecord = messageRepository.findByIdAndAuthStatus(messageID).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        memberService.findByChannelIDAndUserID(messageRecord.channelID, ownerID)
        if (messageRecord.recordStatus == Message.RecordStatus.DELETE_FOR_ALL ||
            messageRecord.recordStatus == Message.RecordStatus.DELETE_FOR_OWNER
        ) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        val membersInChannel = memberService.findAllByChannelID(messageRecord.channelID)
        val userIDs = membersInChannel.map { it.userID }.filter { it != ownerID }

        messageRecord.message = message
        messageRecord.deviceLocalTime = deviceLocalTime
        messageRecord.messageLocation = messageLocation
        messageRecord.subMessage = "updated"
        messageRecord.edited = true

        val updated = messageRepository.saveAndFlush(messageRecord)
        val endpoint = RealtimeEndpoint.UPDATE_MESSAGE_BY_CHANNEL + messageRecord.channelID
        sendRealtimeMessage(userIDs, updated, endpoint)
        return convertResponseMessageDTO(updated, ownerID)
    }

    override fun getMessages(request: RequestDTO<ListRequestDTO>): ListResponseDTO<ResponseMessageDTO> {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val channelID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val listRequestParams = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (listRequestParams.page == 0) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val page = listRequestParams.page - 1
        val sizePerPage = listRequestParams.sizePerPage

        val pageRequest = PageRequest.of(page, sizePerPage, Sort.by(Sort.Direction.DESC, "makerDate"))
        val messages = messageRepository.findAllByChannelIDAndAuthStatus(
            channelID = channelID,
            authStatus = AuthStatus.ACTIVE,
            pageable = pageRequest
        )

        val messageIDs = messages.content.map { it.id }
        val replyIDs = messages.content.mapNotNull { it.replyID }
        val makerIDs = messages.content.map { it.makerID }

        val messageModifier = getMessageModifier(messageIDs, replyIDs, makerIDs , ownerID)
        val attachmentModifier = messageModifier["attachmentModifier"] as Map<String, List<AttachmentDTO>>
        val reactionsModifier = messageModifier["reactionsModifier"] as Map<String, List<MessageReactionDTO>>
        val replyModifier = messageModifier["replyModifier"] as Map<String, ReplyDTO>
        val userModifier = messageModifier["userModifier"] as Map<String, User2DTO>

        val meta = Meta(
            totalElements = messages.totalElements,
            totalPages = messages.totalPages,
            sizePerPage = messages.pageable.pageSize,
            currentPage = messages.pageable.pageNumber + 1,
            numberOfElements = messages.numberOfElements,
            last = messages.isLast
        )

        return ListResponseDTO(
            messages.content.map {
                it.asResponseMessageDTO(attachmentModifier, reactionsModifier, replyModifier, userModifier, ownerID)
            },
            meta
        )
    }

    @Transactional
    override fun deleteMessage(request: RequestDTO<String>): ResponseMessageDTO {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val messageID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val payload = request.payload ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val messageRecord = messageRepository.findByIdAndAuthStatus(messageID).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        memberService.findByChannelIDAndUserID(messageRecord.channelID, ownerID)
        val recordStatus = if (payload == "delete_for_all") {
            Message.RecordStatus.DELETE_FOR_ALL
        } else {
            Message.RecordStatus.DELETE_FOR_OWNER
        }

        val membersInChannel = memberService.findAllByChannelID(messageRecord.channelID)
        val userIDs = membersInChannel.map { it.userID }.filter { it != ownerID }

        messageRecord.recordStatus = recordStatus
        messageRecord.subMessage = payload
        val updated = messageRepository.saveAndFlush(messageRecord)
        val endpoint = RealtimeEndpoint.DELETE_MESSAGE_BY_CHANNEL + messageRecord.channelID
        sendRealtimeMessage(userIDs, updated, endpoint)
        return convertResponseMessageDTO(updated, ownerID)
    }

    @Transactional
    override fun readMessage(request: RequestDTO<Unit>): MutableList<AvatarDTO> {
        val ownerID = request.jwtBody.userID ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val messageID = request.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val messageRecord = messageRepository.findById(messageID).orElseThrow {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
        val memberRecord = memberService.findByChannelIDAndUserID(messageRecord.channelID, ownerID)
        memberRecord.unreadCounter = 0
        val messageReaders = MessageReadersEntity(
            messageID = messageID,
            channelID = messageRecord.channelID,
            userID = ownerID
        )
        messageReadersRepository.saveAndFlush(messageReaders)
        memberRepository.saveAndFlush(memberRecord)
        val messageReader = channelService.getMessageReaders(listOf(messageID))
        return messageReader[messageRecord.channelID.toString()] ?: mutableListOf()
    }
}