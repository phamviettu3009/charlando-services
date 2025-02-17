package com.pvt.realtime_service.services

import com.pvt.realtime_service.models.dtos.NotificationMessageDTO
import com.pvt.realtime_service.models.dtos.RabbitMessageDTO
import com.pvt.realtime_service.models.dtos.RealtimeMessageDTO

interface MessageService {
    fun sendMessage(data: RabbitMessageDTO<RealtimeMessageDTO>)
    fun sendNotification(data: RabbitMessageDTO<NotificationMessageDTO>)
    fun wakeUpDevices(data: RabbitMessageDTO<NotificationMessageDTO>)
}