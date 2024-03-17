package com.pvt.channel_service.models.dtos

data class MessageReactionDTO(
    var icon: String,
    var quantity: Int,
    var toOwn: Boolean
)
