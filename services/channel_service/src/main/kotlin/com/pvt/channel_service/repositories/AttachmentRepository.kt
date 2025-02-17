package com.pvt.channel_service.repositories

import com.pvt.channel_service.models.entitys.AttachmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AttachmentRepository: JpaRepository<AttachmentEntity, UUID> {
    @Query("""
        SELECT a.*
        FROM attachment a
        WHERE a.message_id IN (:messageIDs)
        AND a.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun findAllByMessageIDs(@Param("messageIDs") messageIDs: List<UUID>): List<AttachmentEntity>
}