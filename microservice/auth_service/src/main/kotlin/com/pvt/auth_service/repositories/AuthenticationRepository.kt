package com.pvt.auth_service.repositories

import com.pvt.auth_service.models.entitys.AuthenticationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AuthenticationRepository: JpaRepository<AuthenticationEntity, UUID> {
    @Query("SELECT a FROM AuthenticationEntity a WHERE a.userName = :userName AND a.tenantCode = :tenantCode")
    fun findByUserNameAndTenantCode(@Param("userName") userName: String, @Param("tenantCode") tenantCode: String,): Optional<AuthenticationEntity>

    @Query("SELECT a FROM AuthenticationEntity a WHERE a.userID = :userID AND a.tenantCode = :tenantCode")
    fun findByUserIDAndTenantCode(@Param("userID") userID: UUID, @Param("tenantCode") tenantCode: String,): Optional<AuthenticationEntity>
}