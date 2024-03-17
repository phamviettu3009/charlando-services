package com.pvt.auth_service.models.dtos

data class AuthenticationSuccessResponseDTO(
    val accessToken: String,
    val refreshToken: String
)
