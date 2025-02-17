package com.pvt.channel_service.models.entitys

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.constants.Message
import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.utils.converter.DateTimeConverter
import com.pvt.channel_service.utils.converter.MessageLocationConverter
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "message")
@Entity
data class MessageEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "channel_id")
    var channelID: UUID,

    @Column(name = "message")
    var message: String? = null,

    @Column(name = "reply_id")
    var replyID: UUID? = null,

    @Column(name = "edited")
    var edited: Boolean = false,

    @Column(name = "message_edited_time")
    var messageEditedTime: Date? = null,

    @Column(name = "device_local_time")
    var deviceLocalTime: Date? = null,

    @Convert(converter = MessageLocationConverter::class)
    @Column(name = "message_location", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    var messageLocation: LocationDTO? = null,

    @Column(name = "sub_message")
    var subMessage: String? = null,

    @Column(name = "icon_message")
    var iconMessage: String? = null,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,

    @Column(name = "record_status")
    var recordStatus: String? = null,

    @Column(name = "maker_id")
    var makerID: UUID,

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = null,

    @Column(name = "consecutive_messages")
    var consecutiveMessages: Boolean = false,

    @Column(name = "type")
    var type: Int,

    @Column(name = "urls_preview")
    var urlsPreview: String? = null
) {
    private fun <T> contentDisplay(ownerID: UUID, content: T?): T? {
        if (recordStatus == Message.RecordStatus.DELETE_FOR_ALL) return null
        if (recordStatus == Message.RecordStatus.DELETE_FOR_OWNER && makerID == ownerID) return null
        return content
    }

    private fun contentRecordStatus(ownerID: UUID): String? {
        if (recordStatus == Message.RecordStatus.DELETE_FOR_ALL) return "DELETE"
        if (recordStatus == Message.RecordStatus.DELETE_FOR_OWNER && makerID == ownerID) return "DELETE"
        return null
    }

    private fun subMessageDisplay(ownerID: UUID): String? {
        if(subMessage == "delete_for_owner" && makerID != ownerID) return null
        return subMessage
    }

    fun asResponseMessageDTO(
        attachmentModifier:  Map<String, List<AttachmentDTO>>,
        reactionsModifier: Map<String, List<MessageReactionDTO>>,
        replyModifier: Map<String, ReplyDTO>,
        userModifier: Map<String, User2DTO>,
        ownerID: UUID,
        syncID: UUID? = null
    ): ResponseMessageDTO {
        val messageIDKey = id.toString()
        val replyIDKey = replyID.toString()
        val makerIDKey = makerID.toString()
        return ResponseMessageDTO(
            id = id,
            type = type,
            message = contentDisplay(ownerID, message),
            subMessage = subMessageDisplay(ownerID),
            iconMessage = iconMessage,
            messageOnRightSide = ownerID == makerID,
            consecutiveMessages = consecutiveMessages,
            timeOfMessageSentDisplay = DateTimeConverter.convertDisplay(makerDate),
            edited = edited,
            makerDate = makerDate,
            recordStatus = contentRecordStatus(ownerID),
            channelID = channelID,
            reply = contentDisplay(ownerID, replyModifier[replyIDKey]),
            attachments = contentDisplay(ownerID, attachmentModifier[messageIDKey]),
            messageReactions = contentDisplay(ownerID, reactionsModifier[messageIDKey]),
            user = userModifier[makerIDKey],
            sync = true,
            syncID = syncID,
            urlsPreview = urlsPreview?.split(",") ?: listOf()
        )
    }

    fun asResponseShortenMessageDTO(ownerID: UUID): ResponseShortenMessageDTO {
        return ResponseShortenMessageDTO(
            id = id,
            type = type,
            recordStatus = contentRecordStatus(ownerID),
            message = contentDisplay(ownerID, message),
            subMessage = subMessageDisplay(ownerID),
            iconMessage = iconMessage,
            makerDate = makerDate!!,
            timeOfMessageSentDisplay = DateTimeConverter.convertDisplay(makerDate),
            channelID = channelID
        )
    }
}
