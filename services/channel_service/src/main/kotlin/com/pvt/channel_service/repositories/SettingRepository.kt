package com.pvt.channel_service.repositories

import com.pvt.channel_service.models.entitys.SettingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SettingRepository: JpaRepository<SettingEntity, UUID> {
    fun findByUserID(userID: UUID): Optional<SettingEntity>
}