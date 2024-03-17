package com.pvt.auth_service.utils.components

import org.apache.logging.log4j.LogManager
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoggingInterceptor : HandlerInterceptor {
    private val logger = LogManager.getLogger(LoggingInterceptor::class.java)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Log request details
        logger.info("Received request: ${request.method} ${request.requestURI} from ${request.remoteAddr}")
        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        // Log response details
        logger.info("Sent response: ${request.method} ${request.requestURI} with status ${response.status} and exception $ex")
    }
}