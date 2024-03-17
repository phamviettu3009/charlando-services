package com.pvt.channel_service.models.entitys

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.constants.Channel
import com.pvt.channel_service.models.dtos.AvatarDTO
import com.pvt.channel_service.models.dtos.ResponseChannelDTO
import com.pvt.channel_service.models.dtos.ResponseShortenMessageDTO
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "channel")
@Entity
data class ChannelEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "type")
    var type: Int = Channel.Type.SINGLE_TYPE,

    @Column(name = "member_limit")
    var memberLimit: Int = Channel.MemberLimit.SINGLE,

    @Column(name = "avatar")
    var avatar: String? = null,

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

    @Column(name = "last_message_time")
    var lastMessageTime: Date? = null,
) {
    fun asResponseChannelDTO(
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
            isRead = isRead,
            online = channelModifier[channelIDKey]?.get("online") as Boolean,
            message = messageModifier[channelIDKey],
            readers = readers,
            unreadCounter = unreadCounterModifier[channelIDKey] ?: 0,
            sort = lastMessageTime ?: Date(),
            keywords = channelModifier[channelIDKey]?.get("keywords") as String
        )
    }
}