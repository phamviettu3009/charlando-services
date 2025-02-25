package com.pvt.realtime_service.publisher

import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.models.dtos.DeviceTokenDTO
import com.pvt.realtime_service.models.dtos.RabbitMessageDTO
import com.pvt.realtime_service.models.dtos.UserResponseDTO
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RabbitMQProducer {
    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    fun sendNullMessage(routing: String) {
        rabbitTemplate.convertAndSend(RabbitMQ.Exchange.QUEUE_EXCHANGE, routing, RabbitMessageDTO(null))
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
}

fun RabbitMQProducer.receiveUser(queueName: String): RabbitMessageDTO<List<UserResponseDTO>> {
    val responseType = object : ParameterizedTypeReference<RabbitMessageDTO<List<UserResponseDTO>>>() {}
    return rabbitTemplate.receiveAndConvert(queueName, 10000, responseType)
        ?: throw ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT)
}

fun RabbitMQProducer.receiveDeviceTokenDTO(queueName: String): RabbitMessageDTO<List<DeviceTokenDTO>> {
    val responseType = object : ParameterizedTypeReference<RabbitMessageDTO<List<DeviceTokenDTO>>>() {}
    return rabbitTemplate.receiveAndConvert(queueName, 10000, responseType)
        ?: throw ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT)
}