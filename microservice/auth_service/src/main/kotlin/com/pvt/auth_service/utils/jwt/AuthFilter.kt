package com.pvt.auth_service.utils.jwt

import com.pvt.auth_service.models.dtos.JWTBodyDTO
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val userID = request.getHeader("_userID")
        val authID = request.getHeader("_au")

        if (userID != null && authID != null) {
            val jwtBody = JWTBodyDTO(
                userID = UUID.fromString(userID),
                user = request.getHeader("_user"),
                type = request.getHeader("_type"),
                tenantCode = request.getHeader("_tenantCode"),
                deviceID = request.getHeader("_de"),
                authID = UUID.fromString(authID)
            )
            request.setAttribute("jwtBody", jwtBody)
        }

        filterChain.doFilter(request, response)
    }
}