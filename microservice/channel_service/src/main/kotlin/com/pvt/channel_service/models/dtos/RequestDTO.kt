package com.pvt.channel_service.models.dtos

import java.util.UUID

data class RequestDTO<T>(
    val jwtBody: JWTBodyDTO,
    val payload: T? = null,
    val id: UUID? = null
)