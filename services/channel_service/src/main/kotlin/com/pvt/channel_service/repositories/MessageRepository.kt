package com.pvt.channel_service.repositories

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.models.entitys.MessageEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageRepository: JpaRepository<MessageEntity, UUID> {
    fun findByIdAndChannelIDAndAuthStatus(
        id: UUID,
        channelID: UUID,
        authStatus: String = AuthStatus.ACTIVE
    ): Optional<MessageEntity>

    fun findByIdAndAuthStatus(
        id: UUID,
        authStatus: String = AuthStatus.ACTIVE
    ): Optional<MessageEntity>

    fun findAllByChannelIDAndAuthStatus(
        channelID: UUID,
        authStatus: String = AuthStatus.ACTIVE,
        pageable: Pageable
    ): Page<MessageEntity>

    @Query("""
        SELECT m.*
        FROM message m
        WHERE m.id IN (:replyIDs)
        AND m.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun findAllByReplyIDs(@Param("replyIDs") replyIDs: List<UUID>): List<MessageEntity>

    @Query("""
        SELECT m.*
        FROM message m
        WHERE m.channel_id = :channelID
        ORDER BY maker_date DESC
        LIMIT 1
    """, nativeQuery = true)
    fun findLastMessageByChannelID(@Param("channelID") channelID: UUID): Optional<MessageEntity>

    @Query("""   
        SELECT m.*
        FROM message m
        WHERE (m.channel_id, m.maker_date) IN (
            SELECT channel_id, MAX(maker_date) AS max_maker_date
            FROM message
            WHERE channel_id IN (:channelIDs)
            GROUP BY channel_id
        )
        ORDER BY m.maker_date DESC
    """, nativeQuery = true)
    fun findAlLastMessagesByChannelIDs(@Param("channelIDs") channelIDs: List<UUID>): List<MessageEntity>
}