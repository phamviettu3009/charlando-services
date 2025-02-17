package com.pvt.channel_service.services

import com.pvt.channel_service.models.dtos.RequestDTO

interface CallService {
    fun makeCall(request: RequestDTO<Unit>)
}