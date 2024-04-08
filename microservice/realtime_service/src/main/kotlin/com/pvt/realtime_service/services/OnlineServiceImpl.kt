package com.pvt.realtime_service.services

import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.models.dtos.OnlineStatusDTO
import com.pvt.realtime_service.models.dtos.RabbitMessageDTO
import com.pvt.realtime_service.publisher.RabbitMQProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OnlineServiceImpl: OnlineService {
    @Autowired
    private lateinit var rabbitMQProducer: RabbitMQProducer

    override fun onlineStatus(message: OnlineStatusDTO) {
        rabbitMQProducer.sendMessage(message, RabbitMQ.MSC_UPDATE_ONLINE_STATUS_RECORD_USER.route())
    }
}