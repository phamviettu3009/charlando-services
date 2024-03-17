package com.pvt.base_service.models

import org.springframework.http.HttpStatus

data class JwtFilterException(
    val status: HttpStatus,
    val message: String
)