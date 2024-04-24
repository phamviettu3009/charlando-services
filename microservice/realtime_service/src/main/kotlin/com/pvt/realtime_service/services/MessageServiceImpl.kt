package com.pvt.realtime_service.services

import com.pvt.realtime_service.constants.RabbitMQ
import com.pvt.realtime_service.models.dtos.NotificationMessageDTO
import com.pvt.realtime_service.models.dtos.RabbitMessageDTO
import com.pvt.realtime_service.models.dtos.RealtimeMessageDTO
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(
    val socketIOService: SocketIOService,
    val firebaseMessagingService: FirebaseMessagingService
): MessageService {
    @RabbitListener(queues = [RabbitMQ.Listener.MSCMN_SEND_REALTIME_MESSAGE])
    override fun sendMessage(data: RabbitMessageDTO<RealtimeMessageDTO>) {
        val message = data.message?.message ?: throw Exception("Message not found!")
        val endpoint = data.message.endpoint
        val receiver = data.message.receiverID

        socketIOService.sendMessage(message, endpoint, receiver)
    }

    @RabbitListener(queues = [RabbitMQ.Listener.MSCMN_SEND_NOTIFICATION_MESSAGE])
    override fun sendNotification(data: RabbitMessageDTO<NotificationMessageDTO>) {
        val message = data.message?.message ?: throw Exception("Message not found!")
        val receivers = data.message.receiverIDs

        firebaseMessagingService.sendNotification(message, receivers)
    }
}