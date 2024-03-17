package com.pvt.resource_service.models.dtos

import java.util.*

data class JWTBodyDTO(
    val user: String?,
    val userID: UUID?,
    val tenantCode: String?,
    val deviceID: String?,
    val type: String?,
    val authID: UUID?,
)
