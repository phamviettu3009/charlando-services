package com.pvt.auth_service.repositories

import com.pvt.auth_service.models.entitys.TenantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TenantRepository: JpaRepository<TenantEntity, UUID> {
    @Query("SELECT t FROM TenantEntity t WHERE t.tenantCode = :tenantCode")
    fun findByTenantCode(@Param("tenantCode") tenantCode: String): Optional<TenantEntity>
}