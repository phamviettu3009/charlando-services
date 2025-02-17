package com.pvt.channel_service.models.entitys

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "message_readers")
@Entity
data class MessageReadersEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "message_id")
    var messageID: UUID,

    @Column(name = "channel_id")
    var channelID: UUID,

    @Column(name = "user_id")
    var userID: UUID,

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = null,
)
