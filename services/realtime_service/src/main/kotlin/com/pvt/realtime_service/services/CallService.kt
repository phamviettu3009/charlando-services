package com.pvt.realtime_service.services

import java.util.*

interface CallService {
    fun wakeUpDevices(message: Map<String, Any>, receivers: List<UUID>)
}