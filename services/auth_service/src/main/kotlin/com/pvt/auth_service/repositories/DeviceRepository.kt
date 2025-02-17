package com.pvt.auth_service.repositories

import com.pvt.auth_service.models.entitys.DeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeviceRepository: JpaRepository<DeviceEntity, UUID> {
    fun findByRefreshToken(refreshToken: String): Optional<DeviceEntity>
    fun findByDeviceIDAndAuthenticationID(deviceID: String, authenticationID: UUID): Optional<DeviceEntity>
    fun findAllByAndAuthenticationID(authenticationID: UUID): List<DeviceEntity>
    fun findByDeviceIDAndUserID(deviceID: String, userID: UUID): Optional<DeviceEntity>
    fun findByAccessToken(accessToken: String): Optional<DeviceEntity>
    fun findAllByUserID(userID: UUID): List<DeviceEntity>

    @Query("SELECT d FROM DeviceEntity d WHERE d.userID IN (:ids) AND d.authStatus = 'ACTIVE'")
    fun findAllDeviceByUserIDs(@Param("ids") ids: List<UUID>): List<DeviceEntity>
}