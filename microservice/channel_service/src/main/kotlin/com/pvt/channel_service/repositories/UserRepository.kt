package com.pvt.channel_service.repositories

import com.pvt.channel_service.models.dtos.MemberResponseDTO
import com.pvt.channel_service.models.entitys.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<UserEntity, UUID> {
    @Query("""
        SELECT 
        u.*
        FROM user_info u
        WHERE u.id IN (:IDs) 
        AND u.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun findByIDs(@Param("IDs") IDs: List<UUID>): List<UserEntity>

    @Query("""
        SELECT u.* FROM user_info u 
        INNER JOIN friend f 
        ON u.id = f.friend_id
        WHERE (LOWER(full_name) LIKE LOWER('%'||:keyword||'%') OR LOWER(phone) = LOWER(:keyword))
        AND u.id NOT IN (
            SELECT m.user_id
            FROM member m
            WHERE m.channel_id = :channelID
        )
        AND f.user_id = :userID
        AND f.record_status = 'FRIEND'
        AND f.auth_status = 'ACTIVE'
        AND u.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun findAllFriendOutsideChannelByChannelIDAndUserIDAndKeyword(
        @Param("channelID") channelID: UUID,
        @Param("userID") userID: UUID,
        @Param("keyword") keyword: String,
        pageable: Pageable
    ): Page<UserEntity>

    @Query(value = """
        SELECT u.* FROM user_info u 
        INNER JOIN friend f 
        ON u.id = f.friend_id
        WHERE (LOWER(full_name) LIKE LOWER('%'||:keyword||'%') OR LOWER(phone) = LOWER(:keyword)) 
        AND f.user_id = :userID
        AND f.record_status = 'FRIEND'
        AND f.auth_status = 'ACTIVE'
        AND u.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun findAllFriendByUserIDAndKeyword(
        @Param("userID") userID: UUID,
        @Param("keyword") keyword: String,
        pageable: Pageable
    ): Page<UserEntity>

    @Query(value = """
        SELECT * FROM user_info
        WHERE (LOWER(full_name) LIKE LOWER('%'||:keyword||'%') OR LOWER(phone) = LOWER(:keyword)) 
        AND auth_status = 'ACTIVE'
        AND id != :userID
    """, nativeQuery = true)
    fun findAllByUserIDAndKeyword(
        @Param("userID") userID: UUID,
        @Param("keyword") keyword: String,
        pageable: Pageable
    ): Page<UserEntity>

    @Query(value = """
        SELECT CAST(u.id AS VARCHAR) id, u.full_name AS fullName, u.avatar AS avatar, u.online AS online, m.role AS role FROM user_info u
        INNER JOIN member m ON m.user_id = u.id
        INNER JOIN channel c ON c.id = m.channel_id
        WHERE c.id = :channelID
        AND u.auth_status = 'ACTIVE'
        AND c.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun findAllUserByChannelID(@Param("channelID") channelID: UUID, pageable: Pageable): Page<MemberResponseDTO>
}