package com.pvt.gateway.configs

import com.pvt.gateway.utils.jwt.AuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
class GatewayConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authFilter(): AuthFilter {
        return AuthFilter()
    }
}