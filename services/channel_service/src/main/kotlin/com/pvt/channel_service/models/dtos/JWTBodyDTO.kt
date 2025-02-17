package com.pvt.channel_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class JWTBodyDTO(
    @JsonProperty("user") val user: String?,
    @JsonProperty("userID") val userID: UUID?,
    @JsonProperty("tenantCode") val tenantCode: String?,
    @JsonProperty("deviceID") val deviceID: String?,
    @JsonProperty("type") val type: String?,
    @JsonProperty("authID") val authID: UUID?,
)
