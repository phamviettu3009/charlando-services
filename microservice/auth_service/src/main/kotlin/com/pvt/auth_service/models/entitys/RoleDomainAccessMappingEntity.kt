package com.pvt.auth_service.models.entitys

import com.pvt.auth_service.constants.AuthStatus
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "role_domain_access_mapping")
@Entity
data class RoleDomainAccessMappingEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "domain_level_access_id")
    var domainLevelAccessID: UUID,

    @Column(name = "role_id")
    var roleID: UUID,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,
)
