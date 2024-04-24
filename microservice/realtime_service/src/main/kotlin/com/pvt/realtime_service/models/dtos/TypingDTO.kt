package com.pvt.realtime_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class TypingDTO(
    @JsonProperty("channelID") var channelID: UUID,
    @JsonProperty("typing") var typing: Boolean
)

fun TypingDTO.asTypingResponseDTO(user: UserResponseDTO): TypingResponseDTO {
    return TypingResponseDTO(
        channelID = channelID,
        typing = typing,
        user = user
    )
}
