package com.pvt.base_service.utils.jwt

import com.pvt.base_service.models.JwtFilterException
import io.jsonwebtoken.*
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import java.security.SignatureException
import java.util.*
import javax.servlet.http.HttpServletRequest

object JwtUtils {
    private const val SECRET_KEY = "ptv_3009@"
    private val logger = LogManager.getLogger(JwtUtils::class.java)

    fun createToken(subject: String): String {
        val now = Date()
        val expiration = Date(now.time + 3600000 * 24 * 10) // 1 hour = 3600000

        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }

    fun getUserIdFromToken(token: String): String {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body.subject;
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    fun validateToken(token: String?): Any {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token)
            return true
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature")
            return JwtFilterException(HttpStatus.FORBIDDEN, "Invalid JWT signature!")
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token")
            return JwtFilterException(HttpStatus.FORBIDDEN, "Invalid JWT token!")
        } catch (e: ExpiredJwtException) {
            logger.error("Expired JWT token")
            return JwtFilterException(HttpStatus.UNAUTHORIZED, "Expired JWT token!")
        } catch (e: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
            return  JwtFilterException(HttpStatus.FORBIDDEN, "Unsupported JWT token!")
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
            return JwtFilterException(HttpStatus.FORBIDDEN, "JWT claims string is empty!")
        }
    }
}