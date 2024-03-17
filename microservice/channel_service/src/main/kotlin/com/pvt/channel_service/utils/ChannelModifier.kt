package com.pvt.channel_service.utils

import com.pvt.channel_service.constants.Channel
import com.pvt.channel_service.models.dtos.MemberInChannelDTO
import com.pvt.channel_service.models.dtos.ResponseShortenMessageDTO
import com.pvt.channel_service.models.entitys.MemberEntity
import com.pvt.channel_service.models.entitys.MessageReadersEntity
import java.util.UUID

object ChannelModifier {
    fun getChannelModifierMap(membersInChannel: List<MemberInChannelDTO>, ownerID: UUID): MutableMap<String, MutableMap<String, Any?>> {
        var channelModifierHashMap = mutableMapOf<String, MutableMap<String, Any?>>()

        for (member in membersInChannel) {
            val channelID = member.channelID.toString()
            if (channelModifierHashMap[channelID] == null) {
                if (member.channelType == Channel.Type.SINGLE_TYPE) {
                    if (member.userID != ownerID) {
                        channelModifierHashMap[channelID] = mutableMapOf(
                            "channelName" to member.fullName,
                            "channelAvatar" to mutableListOf(member.avatar),
                            "online" to member.online,
                            "keywords" to ""
                        )
                    }
                } else {
                    val avatars = if (member.channelAvatar != null) mutableListOf(member.channelAvatar) else mutableListOf(member.avatar)
                    channelModifierHashMap[channelID] = mutableMapOf(
                        "channelName" to member.channelName,
                        "channelAvatar" to avatars,
                        "online" to member.online,
                        "keywords" to if (member.userID != ownerID) member.fullName else ""
                    )
                }
            } else {
                if (member.channelType == Channel.Type.GROUP_TYPE) {
                    val oldAvatars = channelModifierHashMap[channelID]?.get("channelAvatar") as MutableList<String>
                    var oldOnline = channelModifierHashMap[channelID]?.get("online") as Boolean
                    val oldKeywords = channelModifierHashMap[channelID]?.get("keywords") as String
                    if (member.channelAvatar == null && member.avatar != null && oldAvatars.size <= 1) {
                        oldAvatars.add(member.avatar!!)
                    }
                    if (!oldOnline) {
                        oldOnline = member.online
                    }
                    val keyword = if (member.userID != ownerID) member.fullName else ""
                    val newKeywords = if (oldKeywords.isNotEmpty()) "$oldKeywords $keyword" else keyword
                    channelModifierHashMap[channelID]?.set("channelAvatar", oldAvatars)
                    channelModifierHashMap[channelID]?.set("online", oldOnline)
                    channelModifierHashMap[channelID]?.set("keywords", newKeywords)
                }
            }
        }

        return channelModifierHashMap
    }

    fun getMessageModifierMap(lastMessages: List<ResponseShortenMessageDTO>): Map<String, ResponseShortenMessageDTO> {
        return lastMessages.associateBy { it.channelID.toString() }
    }

    fun getUnreadCounterModifierMap(members: List<MemberEntity>): MutableMap<String, Int> {
        val channelModifierHashMap = mutableMapOf<String, Int>()

        for (member in members) {
            val channelIDKey = member.channelID.toString()
            if (channelModifierHashMap[channelIDKey] == null) {
                channelModifierHashMap[channelIDKey] = member.unreadCounter
            }
        }

        return channelModifierHashMap
    }
}