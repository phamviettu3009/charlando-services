package com.pvt.channel_service.repositories

import com.pvt.channel_service.models.entitys.FriendEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FriendRepository: JpaRepository<FriendEntity, UUID> {
    fun findByUserIDAndFriendID(userID: UUID, friendID: UUID): Optional<FriendEntity>
    fun findByUserIDAndFriendIDAndRecordStatus(userID: UUID, friendID: UUID, recordStatus: String): Optional<FriendEntity>

    @Query("""
        SELECT COUNT(*) AS quantity
        FROM friend f
        WHERE f.user_id = :userID
        AND f.record_status = 'FRIEND'
    """, nativeQuery = true)
    fun countFriend(@Param("userID") userID: UUID): Optional<CountFriend>

    interface CountFriend {
        var quantity: Int
    }
}