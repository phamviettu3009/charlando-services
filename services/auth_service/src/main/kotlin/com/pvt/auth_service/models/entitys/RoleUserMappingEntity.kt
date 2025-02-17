package com.pvt.auth_service.models.entitys

import com.pvt.auth_service.constants.AuthStatus
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "role_user_mapping")
@Entity
data class RoleUserMappingEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "user_id")
    var userID: UUID,

    @Column(name = "role_id")
    var roleID: UUID,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,
)
