package com.pvt.channel_service.models.entitys

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.constants.Member
import org.hibernate.annotations.GenericGenerator
import java.util.UUID
import javax.persistence.*

@Table(name = "member")
@Entity
data class MemberEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "user_id")
    var userID: UUID,

    @Column(name = "channel_id")
    var channelID: UUID,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,

    @Column(name = "record_status")
    var recordStatus: String? = null,

    @Column(name = "role")
    var role: String = Member.Role.MEMBER,

    @Column(name = "unread_counter")
    var unreadCounter: Int = 0
)
