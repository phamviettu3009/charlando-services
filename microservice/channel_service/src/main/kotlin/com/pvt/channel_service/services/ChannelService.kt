package com.pvt.channel_service.services

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.ChannelEntity
import java.util.*

interface ChannelService {
    fun createSingleChannel(ownerID: UUID, userID: UUID): ChannelEntity?
    fun getChannel(channelID: UUID): ChannelEntity
    fun getChannel(request: RequestDTO<Unit>): ResponseChannelDTO
    fun getChannel(channelID: UUID, ownerID: UUID): ResponseChannelDTO
    fun getChannels(request: RequestDTO<ListRequestDTO>): ListResponseDTO<ResponseChannelDTO>
    fun createGroupChannel(request: RequestDTO<GroupChannelRequestDTO>): ResponseChannelDTO
    fun updateGroupChannel(request: RequestDTO<GroupChannelUpdateRequestDTO>): ResponseChannelDTO
    fun addMembersToGroupChannel(request: RequestDTO<GroupChannelMembers>): ResponseChannelDTO
    fun removeMembersInGroupChannel(request: RequestDTO<GroupChannelMembers>): ResponseChannelDTO
    fun leaveGroupChannel(request: RequestDTO<Unit>): Any
    fun getMessageReaders(messageIDs: List<UUID>): MutableMap<String, MutableList<AvatarDTO>>
    fun getRole(request: RequestDTO<Unit>): Any
    fun setAdminRole(request: RequestDTO<GroupChannelMembers>): Any
    fun revokeAdminRole(request: RequestDTO<GroupChannelMembers>): Any
    fun setOwnerRole(request: RequestDTO<GroupChannelMember>): Any
    fun getMembersInChannel(request: RequestDTO<ListRequestDTO>): ListResponseDTO<MemberResponseDTO>
}