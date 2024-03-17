package com.pvt.base_service.configs

import com.pvt.base_service.utils.jwt.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
class App {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun jwtFilter(): JwtFilter {
        return JwtFilter()
    }
}