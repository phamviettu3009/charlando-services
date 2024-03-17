package com.pvt.channel_service.utils

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.AttachmentEntity
import com.pvt.channel_service.models.entitys.MessageEntity
import com.pvt.channel_service.models.entitys.UserEntity
import com.pvt.channel_service.repositories.MessageReactionRepository

object MessageModifier {
    fun getAttachmentModifier(attachments: List<AttachmentEntity>): Map<String, List<AttachmentDTO>> {
        var messageModifierHashMap = mutableMapOf<String, MutableList<AttachmentDTO>>()

        for (attachment in attachments) {
            val dto = AttachmentDTO(id = attachment.id, type = attachment.type)
            val messageIDKey = attachment.messageID.toString()
            if (messageModifierHashMap[messageIDKey] == null) {
                messageModifierHashMap[messageIDKey] = mutableListOf(dto)
            } else {
                messageModifierHashMap[messageIDKey]?.add(dto)
            }
        }
        return messageModifierHashMap
    }

    fun getReactionModifier(reactions: List<MessageReactionRepository.MultiCountReaction>): Map<String, List<MessageReactionDTO>> {
        var messageModifierHashMap = mutableMapOf<String, MutableList<MessageReactionDTO>>()

        for (reaction in reactions) {
            val dto = MessageReactionDTO(icon = reaction.icon, quantity = reaction.quantity, toOwn = reaction.toOwn)
            val messageIDKey = reaction.messageID.toString()
            if (messageModifierHashMap[messageIDKey] == null) {
                messageModifierHashMap[messageIDKey] = mutableListOf(dto)
            } else {
                messageModifierHashMap[messageIDKey]?.add(dto)
            }
        }

        return messageModifierHashMap
    }

    fun getReplyModifier(replies: List<MessageEntity>, attachmentModifier: Map<String, List<AttachmentDTO>>): Map<String, ReplyDTO> {
        var messageModifierHashMap = mutableMapOf<String, ReplyDTO>()

        for (reply in replies) {
            val messageIDKey = reply.id.toString()
            val dto = ReplyDTO(id = reply.id, type = reply.type, message = reply.message, attachments = attachmentModifier[messageIDKey])
            messageModifierHashMap[messageIDKey] = dto
        }

        return messageModifierHashMap
    }

    fun getUserModifier(users: List<UserEntity>): Map<String, User2DTO> {
        var messageModifierHashMap = mutableMapOf<String, User2DTO>()

        for (user in users) {
            val userIDKey = user.id.toString()
            val dto = User2DTO(id = user.id, fullName = user.fullName, avatar = user.avatar, online = user.online)
            messageModifierHashMap[userIDKey] = dto
        }

        return messageModifierHashMap
    }
}