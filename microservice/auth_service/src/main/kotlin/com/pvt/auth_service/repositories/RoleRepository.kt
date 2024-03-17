package com.pvt.auth_service.repositories

import com.pvt.auth_service.constants.AuthStatus
import com.pvt.auth_service.models.entitys.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository: JpaRepository<RoleEntity, UUID> {
    fun findByRoleNameAndTenantCodeAndAuthStatus(roleName: String?, tenantCode: String?, authStatus: String = AuthStatus.ACTIVE): Optional<RoleEntity>
}