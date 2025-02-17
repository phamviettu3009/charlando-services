package com.pvt.gateway.utils.jwt

import com.pvt.gateway.models.dtos.JWTBodyDTO
import com.pvt.gateway.models.dtos.JwtValidationDTO
import com.pvt.gateway.utils.components.OkHttp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerWebExchange

class AuthFilter : AbstractGatewayFilterFactory<AuthFilter.Config>(Config::class.java) {
    @Autowired
    lateinit var okHttp: OkHttp

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain ->
            val requestPath = exchange.request.uri.path
            val shouldFilter = shouldApplyAuthFilter(requestPath)

            if (shouldFilter) {
                val jwtToken = JwtUtils.resolveToken(exchange.request)
                val path = getPath(requestPath)
                val method = exchange.request.methodValue
                val jwtBody = validation(jwtToken, path, method)
                val modifiedExchange = modifyRequest(exchange, jwtBody)

                return@GatewayFilter chain.filter(modifiedExchange)
            }

            chain.filter(exchange)
        }
    }

    private fun modifyRequest(exchange: ServerWebExchange, jwtBody: JWTBodyDTO): ServerWebExchange {
        val request = exchange.request
        val newHeaders = HttpHeaders()
        newHeaders.add("_userID", jwtBody.userID.toString())
        newHeaders.add("_user", jwtBody.user)
        newHeaders.add("_type", jwtBody.type)
        newHeaders.add("_tenantCode", jwtBody.tenantCode)
        newHeaders.add("_de", jwtBody.deviceID)
        newHeaders.add("_au", jwtBody.authID.toString())

        val modifiedRequest = request.mutate().headers { headers -> headers.addAll(newHeaders) }.build()
        return exchange.mutate().request(modifiedRequest).build()
    }

    private fun validation(jwtToken: String?, path: String, method: String): JWTBodyDTO {
        val jwtBody = okHttp.executePost(
            "http://auth-service:5500/api/v1/auth/validation-jwt",
            JwtValidationDTO(token = jwtToken, path = path, method = method),
            JWTBodyDTO::class.java
        )

        return jwtBody!!
    }

    private fun shouldApplyAuthFilter(requestPath: String): Boolean {
        val exclusionList = listOf(
            "/api/v1/auth/login-with-email",
            "/api/v1/auth/register-with-email",
            "/api/v1/auth/verify-account",
            "/api/v1/auth/get-new-access-token",
            "/api/v1/auth/resend-verify-code",
            "/api/v1/auth/request-forgot-password",
            "/api/v1/auth/forgot-password"
        )

        return !exclusionList.contains(requestPath)
    }

    private fun getPath(path: String): String {
        val prefix = "/api/v1"
        if (path.startsWith(prefix)) {
            return path.substring(prefix.length)
        }
        return ""
    }

    class Config {

    }
}