package com.pvt.resource_service.utils

import com.pvt.resource_service.models.dtos.JWTBodyDTO
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.asRequestAttribute(): JWTBodyDTO {
    val jwtBody = getAttribute("jwtBody")
    if (jwtBody != null) {
        return jwtBody  as JWTBodyDTO
    }
    throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "jwt attribute not found!")
}