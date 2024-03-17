package com.pvt.resource_service.models.dtos

data class RequestPayloadDTO<T>(
    val jwtBody: JWTBodyDTO,
    val payload: T
)
