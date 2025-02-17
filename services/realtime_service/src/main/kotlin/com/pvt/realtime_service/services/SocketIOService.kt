package com.pvt.realtime_service.services

import java.util.*

interface SocketIOService {
    fun run()
    fun sendMessage(message: Any, endpoint: String, receiver: UUID)
}