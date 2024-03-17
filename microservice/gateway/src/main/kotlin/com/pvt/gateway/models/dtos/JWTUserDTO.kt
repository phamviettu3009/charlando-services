package com.pvt.gateway.models.dtos

import java.util.UUID

data class JWTUserDTO(
    val userID: UUID,
    val token: String,
    val deviceID: String,
    val tenantCode: String
)
