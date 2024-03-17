package com.pvt.auth_service.utils.jwt

import com.pvt.auth_service.models.dtos.JWTBodyDTO
import com.pvt.auth_service.models.dtos.JwtFilterExceptionDTO
import io.jsonwebtoken.*
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.security.SignatureException
import java.util.*
import javax.servlet.http.HttpServletRequest

object JwtUtils {
    private const val SECRET_KEY = "ptv_3009@"
    private val logger = LogManager.getLogger(JwtUtils::class.java)

    //1 hour = 3600000
    fun createJWT(jwtBody: JWTBodyDTO, issuedAt: Date = Date(), expiration: Date = Date(Date().time + 3600000 * 24 * 1)): String {
        val user = jwtBody.user
        val userID = jwtBody.userID.toString()
        val tenantCode = jwtBody.tenantCode
        val type = jwtBody.type
        val deviceID = jwtBody.deviceID
        val authID = jwtBody.authID

        return Jwts.builder()
            .setClaims(mapOf("user" to user, "userID" to userID, "tenantCode" to tenantCode, "type" to type, "de" to deviceID, "au" to authID))
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }

    fun getUserIDFromToken(token: String): String {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body["userID"] as String
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT invalid!")
        }
    }

    fun getBodyFromToken(token: String): JWTBodyDTO {
        try {
            val jwtBody = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body as Map<String, String>
            val userID = jwtBody["userID"]
            val user = jwtBody["user"]
            val type = jwtBody["type"]
            val tenantCode = jwtBody["tenantCode"]
            val deviceID = jwtBody["de"]
            val auth = jwtBody["au"]

            return JWTBodyDTO(
                userID = UUID.fromString(userID),
                user = user,
                type = type,
                tenantCode = tenantCode,
                deviceID = deviceID,
                authID = UUID.fromString(auth)
            )
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT invalid!")
        }
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