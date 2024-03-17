package com.pvt.channel_service.repositories

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.models.dtos.ChannelDTO
import com.pvt.channel_service.models.dtos.MemberInChannelDTO
import com.pvt.channel_service.models.entitys.ChannelEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ChannelRepository: JpaRepository<ChannelEntity, UUID> {
    fun findByIdAndAuthStatus(id: UUID, authStatus: String = AuthStatus.ACTIVE): Optional<ChannelEntity>

    fun findByIdAndTypeAndAuthStatus(
        id: UUID,
        type: Int,
        authStatus: String = AuthStatus.ACTIVE
    ): Optional<ChannelEntity>

    @Query("SELECT c FROM ChannelEntity c WHERE c.id IN (:ids) AND c.authStatus = :authStatus")
    fun findAllByIDsAndAuthStatus(
        @Param("ids") ids: List<UUID>,
        @Param("authStatus") authStatus: String = AuthStatus.ACTIVE
    ): List<ChannelEntity>

    @Query(value = """
        SELECT subquery.*
        FROM (
            SELECT 
            CAST(u.id as varchar) userID, 
            u.full_name as fullName, 
            u.avatar, 
            u.online, 
            CAST(c.id as varchar) channelID, 
            c.type as channelType, 
            c.avatar as channelAvatar,
            c.name as channelName,
            ROW_NUMBER() OVER (PARTITION BY m.channel_id ORDER BY u.id) AS rowNum
            FROM user_info u
            JOIN member m ON u.id = m.user_id
            JOIN channel c ON m.channel_id = c.id
            WHERE m.channel_id IN (:ids)
        ) AS subquery
    """, nativeQuery = true)
    fun findAllByChannelIDs(@Param("ids") ids: List<UUID>): List<MemberInChannelDTO>

    @Query(value = """
        SELECT subquery.*
        FROM (
            SELECT CAST(u.id as varchar) userID, 
            u.full_name as fullName, 
            u.avatar, 
            u.online, 
            CAST(c.id as varchar) channelID, 
            c.type as channelType, 
            c.avatar as channelAvatar,
            c.name as channelName,
            ROW_NUMBER() OVER (PARTITION BY m.channel_id ORDER BY u.id) AS rowNum
            FROM user_info u
            JOIN member m ON u.id = m.user_id
            JOIN channel c ON m.channel_id = c.id
            WHERE m.channel_id IN (:ids)
        ) AS subquery
        WHERE rowNum <= 2
    """, nativeQuery = true)
    fun findTwoMemberInChannelByChannelIDs(@Param("ids") ids: List<UUID>): List<MemberInChannelDTO>

    @Query(value = """
        SELECT CAST(c.id as varchar) id, c.name, c.type, c.record_status AS recordStatus , c.avatar, c.last_message_time AS lastMessageTime
        FROM channel c
        INNER JOIN member m ON c.id = m.channel_id
        INNER JOIN user_info u ON m.user_id = u.id
        WHERE (
            (
                (LOWER(unaccent(u.full_name)) LIKE LOWER(unaccent('%'||:keyword||'%')) AND u.id != :userID)
                OR LOWER(unaccent(c.name)) LIKE LOWER(unaccent('%'||:keyword||'%'))
            )
            AND c.id IN (
                SELECT channel_id
                FROM member
                WHERE user_id = :userID
                AND member.auth_status = 'ACTIVE'
            )
        )
        AND c.auth_status = 'ACTIVE'
        GROUP BY c.id
        ORDER BY c.last_message_time DESC NULLS LAST
    """, nativeQuery = true)
    fun findAllByUserIDAndKeyword(@Param("userID") userID: UUID, @Param("keyword") keyword: String, pageable: Pageable): Page<ChannelDTO>

    @Query("""
        SELECT CAST(c.id as varchar) id, c.name, c.type, c.record_status AS recordStatus , c.avatar, c.last_message_time AS lastMessageTime
        FROM channel c
        INNER JOIN member m1 ON c.id = m1.channel_id
        INNER JOIN member m2 ON c.id = m2.channel_id
        WHERE m1.user_id = :ownerID
        AND m2.user_id = :userID
        AND c.type = 1
    """, nativeQuery = true)
    fun findByOwnerIDAndUserID(@Param("ownerID") ownerID: UUID, @Param("userID") userID: UUID): Optional<ChannelDTO>
}