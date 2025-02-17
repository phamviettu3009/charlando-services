package com.pvt.realtime_service.services

import com.pvt.realtime_service.models.dtos.OnlineStatusDTO
import com.pvt.realtime_service.models.dtos.TypingDTO
import com.pvt.realtime_service.models.dtos.UserResponseDTO

interface OnlineService {
    fun onlineStatus(message: OnlineStatusDTO)
    fun typing(typing: TypingDTO): List<UserResponseDTO>
}