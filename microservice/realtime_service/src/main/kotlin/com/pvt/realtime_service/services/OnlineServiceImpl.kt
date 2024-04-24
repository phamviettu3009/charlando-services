package com.pvt.realtime_service.services

import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.models.dtos.OnlineStatusDTO
import com.pvt.realtime_service.models.dtos.TypingDTO
import com.pvt.realtime_service.models.dtos.UserResponseDTO
import com.pvt.realtime_service.publisher.RabbitMQProducer
import com.pvt.realtime_service.publisher.receiveUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OnlineServiceImpl: OnlineService {
    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    override fun onlineStatus(message: OnlineStatusDTO) {
        rabbitMQProducer.sendMessage(message, RabbitMQ.MSC_UPDATE_ONLINE_STATUS_RECORD_USER.route())
    }

    override fun typing(typing: TypingDTO): List<UserResponseDTO> {
        rabbitMQProducer.sendMessage(typing, RabbitMQ.MSC_TYPING.route())
        val res = rabbitMQProducer.receiveUser(RabbitMQ.MSC_TYPING.callbackQueue())
        return res.message ?: listOf()
    }
}