package com.pvt.auth_service.models.dtos

import java.util.*

data class JwtUserDTO(
    val userID: UUID,
    val token: String,
    val deviceID: String,
    val tenantCode: String
)
