package com.pvt.gateway.utils.jwt

import com.pvt.gateway.models.dtos.JwtFilterExceptionDTO
import io.jsonwebtoken.*
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ResponseStatusException
import java.security.SignatureException
import java.util.*
import javax.servlet.http.HttpServletRequest

object JwtUtils {
    private const val SECRET_KEY = "ptv_3009@"
    private val logger = LogManager.getLogger(JwtUtils::class.java)

    fun getUserIDFromToken(token: String): String {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body["userID"] as String
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT invalid!")
        }
    }

    fun getBodyFromToken(token: String): Map<String, String> {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body as Map<String, String>
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT invalid!")
        }
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader("Authorization")
        return if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            authorizationHeader.substring(7)
        } else null
    }

    fun resolveToken(request: ServerHttpRequest): String? {
        val authorizationHeader = request.headers.getFirst("Authorization")
        if (!authorizationHeader.isNullOrBlank() && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7)
        }
        return null
    }

    fun validateToken(token: String?): Any {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature")
            return JwtFilterExceptionDTO(HttpStatus.UNAUTHORIZED, "Invalid JWT signature!")
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token")
            return JwtFilterExceptionDTO(HttpStatus.UNAUTHORIZED, "Invalid JWT token!")
        } catch (e: ExpiredJwtException) {
            logger.error("Expired JWT token")
            return JwtFilterExceptionDTO(HttpStatus.UNAUTHORIZED, "Expired JWT token!")
        } catch (e: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
            return JwtFilterExceptionDTO(HttpStatus.UNAUTHORIZED, "Unsupported JWT token!")
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
            return JwtFilterExceptionDTO(HttpStatus.UNAUTHORIZED, "JWT claims string is empty!")
        } catch (e: Exception) {
            logger.error("Other JWT exception")
            return JwtFilterExceptionDTO(HttpStatus.UNAUTHORIZED, "JWT exception!")
        }
    }
}