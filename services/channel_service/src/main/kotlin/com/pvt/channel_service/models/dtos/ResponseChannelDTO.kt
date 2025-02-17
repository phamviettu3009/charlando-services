package com.pvt.channel_service.models.dtos

import java.util.*

data class ResponseChannelDTO(
    val id: UUID,
    val name: String?,
    var type: Int,
    var avatars: List<String>,
    var recordStatus: String?,
    val read: Boolean? = false,
    val online: Boolean? = false,
    val message: ResponseShortenMessageDTO?,
    val readers: MutableList<AvatarDTO>,
    val unreadCounter: Int = 0,
    val sort: Date,
    val keywords: String,
)