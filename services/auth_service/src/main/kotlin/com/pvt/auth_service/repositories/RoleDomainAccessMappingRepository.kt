package com.pvt.auth_service.repositories

import com.pvt.auth_service.constants.AuthStatus
import com.pvt.auth_service.models.entitys.RoleDomainAccessMappingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleDomainAccessMappingRepository: JpaRepository<RoleDomainAccessMappingEntity, UUID> {
    @Query("SELECT d FROM RoleDomainAccessMappingEntity d WHERE d.roleID IN (:roleIDs) AND d.authStatus = :authStatus")
    fun findAllByRoleIDsAndAuthStatus(
        @Param("roleIDs") roleIDs: List<UUID>,
        @Param("authStatus") authStatus: String = AuthStatus.ACTIVE
    ): List<RoleDomainAccessMappingEntity>
}