package com.pvt.gateway.models.dtos

import org.springframework.http.HttpStatus

data class JwtFilterExceptionDTO(
    val status: HttpStatus,
    val message: String
)