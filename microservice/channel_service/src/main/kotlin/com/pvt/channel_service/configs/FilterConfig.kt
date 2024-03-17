package com.pvt.channel_service.configs

import com.pvt.channel_service.models.dtos.JWTBodyDTO
import com.pvt.channel_service.utils.extension.asUUID
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class FilterConfig : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val userID = request.getHeader("_userID")
        val authID = request.getHeader("_au")

        if (userID != null && authID != null) {
            val jwtBody = JWTBodyDTO(
                userID = userID.asUUID(),
                user = request.getHeader("_user"),
                type = request.getHeader("_type"),
                tenantCode = request.getHeader("_tenantCode"),
                deviceID = request.getHeader("_de"),
                authID = authID.asUUID()
            )
            request.setAttribute("jwtBody", jwtBody)
        }

        filterChain.doFilter(request, response)
    }
}