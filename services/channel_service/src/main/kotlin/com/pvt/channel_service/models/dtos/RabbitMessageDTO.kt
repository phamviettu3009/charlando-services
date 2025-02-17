package com.pvt.channel_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class RabbitMessageDTO<T>(
    @JsonProperty("message") val message: T?
)
