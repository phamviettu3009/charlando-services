package com.pvt.realtime_service.services

import com.pvt.realtime_service.models.dtos.OnlineStatusDTO
import com.pvt.realtime_service.models.dtos.RabbitMessageDTO
import java.util.*

interface OnlineService {
    fun onlineStatus(message: OnlineStatusDTO)
}