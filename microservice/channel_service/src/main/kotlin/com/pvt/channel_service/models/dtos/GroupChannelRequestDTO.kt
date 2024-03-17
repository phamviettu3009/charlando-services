package com.pvt.channel_service.models.dtos

data class GroupChannelRequestDTO(
    val memberIDs: List<String>,
    val groupName: String
)