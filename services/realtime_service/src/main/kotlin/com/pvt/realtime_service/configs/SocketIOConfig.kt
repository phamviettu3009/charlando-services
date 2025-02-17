package com.pvt.realtime_service.configs

import com.corundumstudio.socketio.AuthorizationListener
import com.corundumstudio.socketio.AuthorizationResult
import com.corundumstudio.socketio.SocketIOServer
import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.publisher.RabbitMQProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SocketIOConfig {
    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    @Bean
    fun socketIOServer(): SocketIOServer {
        val config = com.corundumstudio.socketio.Configuration()
        config.port = 5504
        config.authorizationListener = AuthorizationListener { data ->
            val token = data.httpHeaders.get("Authorization")
            val response = rabbitMQProducer.sendAndCallbackMessage<String>(
                token,
                RabbitMQ.MSCMN_VALIDATION_JWT.route(),
                RabbitMQ.MSCMN_VALIDATION_JWT.callbackQueue()
            )
            val userID = response.message
            if (userID != null) {
                data.httpHeaders.set("userID", userID)
                AuthorizationResult(true)
            } else {
                AuthorizationResult(false)
            }
        }
        return SocketIOServer(config)
    }
}