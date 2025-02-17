package com.pvt.auth_service.services

import com.pvt.auth_service.models.dtos.FirebaseDeviceToken
import com.pvt.auth_service.models.dtos.RabbitMessageDTO
import java.util.UUID

interface DeviceService {
    fun getDevicesByUserIDs(userIDs: RabbitMessageDTO<List<UUID>>)
    fun updateFirebaseToken(firebaseDeviceToken: FirebaseDeviceToken, userID: UUID): String
}