package com.pvt.channel_service.models.dtos

import java.util.*

interface ChannelDTO {
    val id: UUID
    val name: String?
    var type: Int
    var recordStatus: String?
    val avatar: String?
    val lastMessageTime: Date?
}

fun ChannelDTO.asResponseChannelDTO(
    channelModifier: MutableMap<String, MutableMap<String, Any?>>,
    messageModifier: Map<String, ResponseShortenMessageDTO>,
    messageReaderModifier: MutableMap<String, MutableList<AvatarDTO>>,
    unreadCounterModifier: MutableMap<String, Int>,
    ownerID: UUID
): ResponseChannelDTO {
    val channelIDKey = id.toString()
    val readers = messageReaderModifier[channelIDKey] ?: mutableListOf()
    val isRead = readers.any { it.userID ==  ownerID}
    return ResponseChannelDTO(
        id = id,
        name = channelModifier[channelIDKey]?.get("channelName") as String,
        type = type,
        avatars =  channelModifier[channelIDKey]?.get("channelAvatar") as List<String>,
        recordStatus = recordStatus,
        read = isRead,
        online = channelModifier[channelIDKey]?.get("online") as Boolean,
        message = messageModifier[channelIDKey],
        readers = readers,
        unreadCounter = unreadCounterModifier[channelIDKey] ?: 0,
        sort = lastMessageTime ?: Date(),
        keywords = channelModifier[channelIDKey]?.get("keywords") as String
    )
}