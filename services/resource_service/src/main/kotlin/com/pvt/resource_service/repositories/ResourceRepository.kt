package com.pvt.resource_service.repositories

import com.pvt.resource_service.models.entitys.ResourceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ResourceRepository: JpaRepository<ResourceEntity, UUID> {
    @Query("SELECT r FROM ResourceEntity r WHERE r.id = :id AND r.authStatus = :authStatus")
    fun findByIdAndAuthStatus(@Param("id") id: UUID, @Param("authStatus") authStatus: String): Optional<ResourceEntity>

    @Query("""
        SELECT r.*
        FROM resource r
        WHERE r.id IN (:IDs)
        AND r.maker_id = :userID
        AND r.auth_status = 'ACTIVE'
    """, nativeQuery = true)
    fun findAllByIDsAndUserID(@Param("IDs") IDs: List<UUID>, @Param("userID") userID: UUID): List<ResourceEntity>

    @Query("""
        SELECT r.*
        FROM resource r
        WHERE r.id = (
            SELECT t.thumbnail_id
            FROM thumbnail t
            WHERE t.video_id = :videoID
        ) 
    """, nativeQuery = true)
    fun findThumbnailByVideoID(@Param("videoID") videoID: UUID): Optional<ResourceEntity>
}