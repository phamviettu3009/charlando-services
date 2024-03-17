package com.pvt.auth_service.configs

import com.pvt.auth_service.utils.jwt.AuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
class AuthConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authFilter(): AuthFilter {
        return AuthFilter()
    }
}