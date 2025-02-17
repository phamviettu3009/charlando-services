package com.pvt.realtime_service.models.dtos

import java.util.*

data class TypingResponseDTO(
    val channelID: UUID,
    val typing: Boolean,
    val user: UserResponseDTO
)
