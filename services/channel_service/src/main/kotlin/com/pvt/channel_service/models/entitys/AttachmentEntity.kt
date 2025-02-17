package com.pvt.channel_service.models.entitys

import com.pvt.channel_service.constants.AuthStatus
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Table(name = "attachment")
@Entity
data class AttachmentEntity(
    @Id
    var id: UUID,

    @Column(name = "message_id")
    var messageID: UUID,

    @Column(name = "type")
    var type: Int,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,

    @Column(name = "maker_id")
    var makerID: UUID,

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = null,
)
