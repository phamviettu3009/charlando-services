package com.pvt.channel_service.models.entitys

import com.pvt.channel_service.constants.AuthStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "message_reaction")
@Entity
data class MessageReactionEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "message_id")
    var messageID: UUID,

    @Column(name = "icon")
    var icon: String,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,

    @Column(name = "maker_id")
    var makerID: UUID,

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = null,
)
