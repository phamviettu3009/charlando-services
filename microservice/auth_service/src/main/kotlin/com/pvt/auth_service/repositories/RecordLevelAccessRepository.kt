package com.pvt.auth_service.repositories

import com.pvt.auth_service.constants.AuthStatus
import com.pvt.auth_service.models.entitys.RecordLevelAccessEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RecordLevelAccessRepository: JpaRepository<RecordLevelAccessEntity, UUID> {
    fun findByAccessContentAndMethodAndAuthStatus(
        accessContent: String,
        method: String ,
        authStatus: String = AuthStatus.ACTIVE
    ): Optional<RecordLevelAccessEntity>

    @Query("""
        SELECT r.*
        FROM record_level_access r
        WHERE r.access_content ~ :accessContent
        AND r.method = :method
    """, nativeQuery = true)
    fun findAllByAccessContentFormatAndMethod(
        @Param("accessContent") accessContent: String,
        @Param("method") method: String,
    ): List<RecordLevelAccessEntity>
    // accessContent = '^/channel/group/[^/]+/remove-members$'
}