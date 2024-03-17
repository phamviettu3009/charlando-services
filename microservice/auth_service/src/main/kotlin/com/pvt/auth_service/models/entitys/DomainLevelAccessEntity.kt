package com.pvt.auth_service.models.entitys

import com.pvt.auth_service.constants.AuthStatus
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "domain_level_access")
@Entity
data class DomainLevelAccessEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "access_content")
    var accessContent: String,

    @Column(name = "method")
    var method: String,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,
)
