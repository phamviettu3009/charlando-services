package com.pvt.auth_service.utils.cache

import com.pvt.auth_service.models.dtos.JwtUserDTO
import java.util.*

object JWTCache {
    private var jwtCaches:  HashMap<String, MutableMap<String, String>> = hashMapOf()

    fun hasCache(jwt: JwtUserDTO): Boolean {
        val userIDKey = jwt.userID.toString()
        val deviceIDKey = jwt.deviceID
        val tenantCodeKey = jwt.tenantCode
        return jwtCaches[userIDKey]?.get(deviceIDKey + tenantCodeKey) == jwt.token
    }

    fun setCache(jwt: JwtUserDTO) {
        val userIDKey = jwt.userID.toString()
        val deviceIDKey = jwt.deviceID
        val tenantCodeKey = jwt.tenantCode
        if (jwtCaches[userIDKey] != null) {
            jwtCaches[userIDKey]?.put(deviceIDKey + tenantCodeKey, jwt.token)
        } else {
            jwtCaches[userIDKey] = mutableMapOf(deviceIDKey + tenantCodeKey to jwt.token)
        }
    }

    fun removeCacheByUserID(userID: UUID) {
        jwtCaches.remove(userID.toString())
    }

    fun removeCacheByUserIDAndDeviceIDAndTenantCode(userID: UUID, deviceID: String, tenantCode: String) {
        jwtCaches[userID.toString()]?.remove(deviceID + tenantCode)
    }
}