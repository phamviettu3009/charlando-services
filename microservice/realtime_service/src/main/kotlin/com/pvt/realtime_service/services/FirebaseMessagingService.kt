package com.pvt.realtime_service.services

import java.util.*

interface FirebaseMessagingService {
    fun sendNotification(message: Map<String, String>, receivers: List<UUID>)
}