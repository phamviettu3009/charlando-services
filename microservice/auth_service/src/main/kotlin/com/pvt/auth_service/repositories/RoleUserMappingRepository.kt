package com.pvt.auth_service.repositories

import com.pvt.auth_service.constants.AuthStatus
import com.pvt.auth_service.models.entitys.RoleUserMappingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleUserMappingRepository: JpaRepository<RoleUserMappingEntity, UUID> {
    fun findAllByUserIDAndAuthStatus(userID: UUID, authStatus: String = AuthStatus.ACTIVE): List<RoleUserMappingEntity>
}