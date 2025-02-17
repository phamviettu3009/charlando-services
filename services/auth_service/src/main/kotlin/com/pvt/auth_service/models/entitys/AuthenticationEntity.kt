package com.pvt.auth_service.models.entitys

import com.pvt.auth_service.constants.AuthStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "authentication")
@Entity
data class AuthenticationEntity(
    @Id
    @GeneratedValue(generator = "uuid", strategy = GenerationType.AUTO)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = null,

    @Column(name = "user_id")
    var userID: UUID,

    @Column(name = "user_name")
    var userName: String? = null,

    @Column(name = "hash_password")
    var hashPassword: String? = null,

    @Column(name = "tenant_code")
    var tenantCode: String? = null,

    @Column(name = "record_status")
    var recordStatus: String? = null,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,

    @Column(name = "verify_code")
    var verifyCode: String? = null,

    @Column(name = "verify_code_maker_date")
    var verifyCodeMakerDate: Date? = null
)
