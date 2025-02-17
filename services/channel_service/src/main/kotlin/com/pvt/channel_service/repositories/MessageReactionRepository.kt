package com.pvt.channel_service.repositories

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.models.entitys.MessageReactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageReactionRepository: JpaRepository<MessageReactionEntity, UUID> {
    fun findByMessageIDAndIconAndMakerIDAndAuthStatus(
        messageID: UUID,
        icon: String,
        makerID: UUID,
        authStatus: String = AuthStatus.ACTIVE
    ): Optional<MessageReactionEntity>

    @Query("""
        SELECT
            COUNT(*) AS quantity,
            COUNT(*) FILTER (WHERE mr.maker_id = :makerID) > 0 AS toOwn
        FROM
            message_reaction mr
        WHERE
            mr.message_id = :messageID
            AND mr.icon = :icon
            AND mr.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun countByMessageIDAndIconWithMakerID(
        @Param("messageID") messageID: UUID,
        @Param("icon") icon: String,
        @Param("makerID") makerID: UUID
    ): CountReaction

    @Query("""
        SELECT
            CAST(mr.message_id AS varchar) messageID,
            mr.icon AS icon,
            COUNT(*) AS quantity,
            COUNT(*) FILTER (WHERE mr.maker_id = :makerID) > 0 AS toOwn
        FROM
            message_reaction mr
        WHERE
            mr.message_id in (:messageIDs)
            AND mr.auth_status = 'ACTIVE'
        GROUP BY
            mr.message_id, mr.icon
    """, nativeQuery = true)
    fun countAllByMessageIDsWithMakerID(
        @Param("messageIDs") messageIDs: List<UUID>,
        @Param("makerID") makerID: UUID
    ): List<MultiCountReaction>

    interface CountReaction {
        val quantity: Int
        val toOwn: Boolean
    }

    interface MultiCountReaction {
        val messageID: UUID
        val icon: String
        val quantity: Int
        val toOwn: Boolean
    }
}