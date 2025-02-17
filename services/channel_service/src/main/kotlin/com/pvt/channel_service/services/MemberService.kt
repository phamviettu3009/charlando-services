package com.pvt.channel_service.services

import com.pvt.channel_service.models.entitys.MemberEntity
import org.springframework.data.domain.PageRequest
import java.util.UUID

interface MemberService {
    fun findByChannelIDAndUserID(channelID: UUID, userID: UUID): MemberEntity
    fun findAllByChannelID(channelID: UUID): List<MemberEntity>
}