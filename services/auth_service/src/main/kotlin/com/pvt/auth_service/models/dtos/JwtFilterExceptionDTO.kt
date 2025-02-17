package com.pvt.auth_service.models.dtos

import org.springframework.http.HttpStatus

data class JwtFilterExceptionDTO(
    val status: HttpStatus,
    val message: String
)