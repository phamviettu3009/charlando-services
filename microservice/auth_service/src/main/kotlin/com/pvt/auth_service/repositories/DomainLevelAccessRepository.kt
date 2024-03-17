package com.pvt.auth_service.repositories

import com.pvt.auth_service.constants.AuthStatus
import com.pvt.auth_service.models.entitys.DomainLevelAccessEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DomainLevelAccessRepository: JpaRepository<DomainLevelAccessEntity, UUID> {
    @Query("SELECT d FROM DomainLevelAccessEntity d WHERE d.id IN (:ids) AND d.authStatus = :authStatus")
    fun findAllByIDsAndAuthStatus(
        @Param("ids") ids: List<UUID>,
        @Param("authStatus") authStatus: String = AuthStatus.ACTIVE
    ): List<DomainLevelAccessEntity>
}