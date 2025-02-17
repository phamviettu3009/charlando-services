package com.pvt.auth_service.models.dtos

import java.util.*

data class DeviceDTO(
    val deviceID: String,
    val deviceName: String,
    val deviceSystemName: String,
    val os: String,
    val userID: UUID,
    val login: Boolean,
    var mostRecentLoginTime: Date?,
    var mostRecentLogoutTime: Date?,
)
