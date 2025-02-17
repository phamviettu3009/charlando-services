package com.pvt.base_service.utils.jwt

import com.pvt.base_service.models.JwtFilterException
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val url = request.requestURI

        when(true) {
            url.endsWith("/login"),
            url.endsWith("/register-with-email"),
            url.endsWith("/verify-account"),
            url.endsWith("/update-account-after-verify"),
            url.endsWith("/resend-verify-code") -> {
                filterChain.doFilter(request, response)
                return
            }
        }

        val token = JwtUtils.resolveToken(request)
        val result = JwtUtils.validateToken(token)

        if (result is JwtFilterException) {
            response.sendError(result.status.value(), result.message)
            return
        }

        val userId = token?.let { JwtUtils.getUserIdFromToken(it) }
        if (userId != null) {
            request.setAttribute("userId", userId)
        }

        filterChain.doFilter(request, response)
    }
}