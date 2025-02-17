package com.pvt.resource_service.models.entitys

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "thumbnail")
@Entity
data class ThumbnailEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "thumbnail_id")
    var thumbnailID: UUID,

    @Column(name = "video_id")
    var videoID: UUID,
)
