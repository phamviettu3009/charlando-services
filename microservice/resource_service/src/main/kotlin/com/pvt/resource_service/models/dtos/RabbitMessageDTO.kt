package com.pvt.resource_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class RabbitMessageDTO<T>(
    @JsonProperty("message") val message: T?
)
