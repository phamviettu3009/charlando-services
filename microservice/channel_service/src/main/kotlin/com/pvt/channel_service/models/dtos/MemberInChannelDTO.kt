package com.pvt.channel_service.models.dtos

import java.util.UUID

interface MemberInChannelDTO {
    var userID: UUID
    var fullName: String
    var avatar: String?
    var online: Boolean
    var channelID: UUID
    var channelType: Int
    var channelAvatar: String?
    var channelName: String?
    var rowNum: Int
}
