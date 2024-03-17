package com.pvt.auth_service.models.entitys

import com.pvt.auth_service.constants.AuthStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "record_level_access")
@Entity
data class RecordLevelAccessEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "access_content")
    var accessContent: String,

    @Column(name = "method")
    var method: String,

    @Column(name = "record_status")
    var recordStatus: String,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = null,
)
