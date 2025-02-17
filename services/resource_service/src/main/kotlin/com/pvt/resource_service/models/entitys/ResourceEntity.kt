package com.pvt.resource_service.models.entitys

import com.pvt.resource_service.constants.AuthStatus
import com.pvt.resource_service.models.dtos.RecordLevelAccessPayloadDTO
import com.pvt.resource_service.models.dtos.ResourceDTO
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.persistence.*

@Table(name = "resource")
@Entity
data class ResourceEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "name")
    var name: String,

    @Column(name = "original_name")
    var originalName: String,

    @Column(name = "extension")
    var extension: String,

    @Column(name = "type")
    var type: Int,

    @Column(name = "maker_id")
    var makerID: UUID?,

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = null,

    @Column(name = "directory_path")
    var directoryPath: String,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE
)

fun ResourceEntity.asDTO(): ResourceDTO {
    return ResourceDTO(id, name, type)
}

fun ResourceEntity.asRecordLevelAccessPayloadDTO(): RecordLevelAccessPayloadDTO {
    return RecordLevelAccessPayloadDTO(
        accessContent = "/resource/get/$id",
        method = "GET",
        recordStatus = getStatusAccessLevelFromDirectoryPath(directoryPath),
        ownerID = makerID,
        userAccessIDs = listOf()
    )
}

fun ResourceEntity.asRecordLevelAccessPayloadDTO(videoID: UUID): RecordLevelAccessPayloadDTO {
    return RecordLevelAccessPayloadDTO(
        accessContent = "/resource/get/$videoID/thumbnail",
        method = "GET",
        recordStatus = getStatusAccessLevelFromDirectoryPath(directoryPath),
        ownerID = makerID,
        userAccessIDs = listOf()
    )
}

private fun getStatusAccessLevelFromDirectoryPath(path: String): String {
    return when {
        "private" in path -> "private"
        "public" in path -> "public"
        else -> throw ResponseStatusException(HttpStatus.EXPECTATION_FAILED)
    }
}
