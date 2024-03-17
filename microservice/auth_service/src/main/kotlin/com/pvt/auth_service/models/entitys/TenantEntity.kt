package com.pvt.auth_service.models.entitys

import com.pvt.auth_service.constants.AuthStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "tenant")
@Entity
data class TenantEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = null,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,

    @Column(name = "tenant_code")
    var tenantCode: String,
)
