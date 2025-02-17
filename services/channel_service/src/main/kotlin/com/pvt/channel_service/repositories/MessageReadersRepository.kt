package com.pvt.channel_service.repositories

import com.pvt.channel_service.models.entitys.MessageReadersEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageReadersRepository: JpaRepository<MessageReadersEntity, UUID> {
    @Query("""
        SELECT mr.*
        FROM message_readers mr
        WHERE mr.message_id IN (:messageIDs)
    """, nativeQuery = true)
    fun findAllByMessageIDs(@Param("messageIDs") messageIDs: List<UUID>): List<MessageReadersEntity>

    fun findByMessageIDAndUserID(messageID: UUID, userID: UUID): Optional<MessageReadersEntity>
}