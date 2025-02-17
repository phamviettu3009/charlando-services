package com.pvt.auth_service.repositories

import com.pvt.auth_service.constants.AuthStatus
import com.pvt.auth_service.models.entitys.UsersWithRecordLevelAccessEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsersWithRecordLevelAccessRepository: JpaRepository<UsersWithRecordLevelAccessEntity, UUID> {
    fun findByRecordLevelAccessIDAndUserIDAndAuthStatus(
        recordLevelAccessID: UUID,
        userID: UUID,
        authStatus: String = AuthStatus.ACTIVE
    ): Optional<UsersWithRecordLevelAccessEntity>

    fun findAllByRecordLevelAccessIDAndAuthStatus(
        recordLevelAccessID: UUID,
        authStatus: String = AuthStatus.ACTIVE
    ): List<UsersWithRecordLevelAccessEntity>

    @Query("""
        SELECT u.*
        FROM users_with_record_level_access u
        WHERE u.user_id IN (:userIDs)
        AND u.record_level_access_id = :recordLevelAccessID
    """, nativeQuery = true)
    fun findAllByUserIDs(
        @Param("userIDs") userIDs: List<UUID>,
        @Param("recordLevelAccessID") recordLevelAccessID: UUID
    ): List<UsersWithRecordLevelAccessEntity>

    @Query("""
        SELECT u.*, r.access_content
        FROM record_level_access r, users_with_record_level_access u
        WHERE r.id = u.record_level_access_id
        AND r.access_content ~ :accessContent
        AND r.method = :method
        AND u.user_id in (:userIDs)
    """, nativeQuery = true)
    fun findAllByAccessContentFormatAndMethodAndUserIDs(
        @Param("accessContent") accessContent: String,
        @Param("method") method: String,
        @Param("userIDs") userIDs: List<UUID>,
    ): List<UsersWithRecordLevelAccessEntity>
    // accessContent = '^/channel/group/[^/]+/remove-members$'
}