package com.pvt.channel_service.repositories

import com.pvt.channel_service.models.entitys.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository: JpaRepository<MemberEntity, UUID> {
    fun findAllByChannelID(channelID: UUID): List<MemberEntity>

    @Query("""
        SELECT m FROM MemberEntity m WHERE m.channelID = :channelID AND m.userID IN (:userIDs)
    """)
    fun findAllByChannelIDAndUserIDs(
        @Param("channelID") channelID: UUID,
        @Param("userIDs") userIDs: List<UUID>
    ): List<MemberEntity>

    @Query("""
        SELECT m.*
        FROM member m, user_info u
        WHERE m.channel_id = :channelID
        AND m.user_id = :userID
        AND u.id = m.user_id
        AND u.auth_status = 'ACTIVE'
        AND m.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun findByChannelIDAndUserID(
        @Param("channelID") channelID: UUID,
        @Param("userID") userID: UUID
    ): Optional<MemberEntity>

    @Query("""
        SELECT m.*
        FROM member m
        WHERE m.channel_id IN (:channelIDs)
        AND m.user_id = :userID
    """, nativeQuery = true)
    fun findAllByChannelIDsAndUserID(
        @Param("channelIDs") channelIDs: List<UUID>,
        @Param("userID") userID: UUID
    ): List<MemberEntity>
}