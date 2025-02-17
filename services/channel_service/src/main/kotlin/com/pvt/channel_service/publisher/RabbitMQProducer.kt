package com.pvt.channel_service.publisher

import com.pvt.channel_service.constants.RabbitMQ
import com.pvt.channel_service.models.dtos.RabbitMessageDTO
import com.pvt.channel_service.models.dtos.UserDTO
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RabbitMQProducer {
    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate

    fun sendMessageNull(routing: String) {
        rabbitTemplate.convertAndSend(RabbitMQ.Exchange.QUEUE_EXCHANGE, routing)
    }

    fun <T>sendAndCallbackMessage(message: Any, routing: String, callbackQueueName: String): RabbitMessageDTO<T> {
        sendMessage(message, routing)
        val response: RabbitMessageDTO<T> = receiveMessage(callbackQueueName)
        if (response.message is String && response.message == "Compensation") throw Exception("Rollback event!")
        return response
    }

    fun sendMessage(message: Any, routing: String) {
        rabbitTemplate.convertAndSend(RabbitMQ.Exchange.QUEUE_EXCHANGE, routing, RabbitMessageDTO(message))
    }

    fun <T>receiveMessage(queueName: String): RabbitMessageDTO<T> {
        val responseType = object : ParameterizedTypeReference<RabbitMessageDTO<T>>() {}
        return rabbitTemplate.receiveAndConvert(queueName, 10000, responseType)
            ?: throw ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT)
    }

    fun receiveUserMessage(queueName: String): RabbitMessageDTO<UserDTO> {
        val responseType = object : ParameterizedTypeReference<RabbitMessageDTO<UserDTO>>() {}
        return rabbitTemplate.receiveAndConvert(queueName, 10000, responseType)
            ?: throw ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT)
    }
}