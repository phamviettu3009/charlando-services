package com.pvt.auth_service.models.entitys

import com.pvt.auth_service.constants.AuthStatus
import com.pvt.auth_service.models.dtos.DeviceDetailConverter
import com.pvt.auth_service.models.dtos.DeviceDetailDTO
import org.hibernate.annotations.ColumnTransformer
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Table(name = "device")
@Entity
data class DeviceEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "maker_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    var makerDate: Date? = Date(),

    @Column(name = "checker_date")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    var checkerDate: Date? = Date(),

    @Column(name = "device_id")
    var deviceID: String?,

    @Column(name = "device_name")
    var deviceName: String? = "",

    @Column(name = "os")
    var os: String? = "",

    @Column(name = "brand")
    var brand: String? = "",

    @Convert(converter = DeviceDetailConverter::class)
    @Column(name = "detail", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    var detail: DeviceDetailDTO? = DeviceDetailDTO(),

    @Column(name = "device_type")
    var deviceType: String? = "",

    @Column(name = "user_id")
    var userID: UUID?,

    @Column(name = "access_token")
    var accessToken: String?,

    @Column(name = "refresh_token")
    var refreshToken: String?,

    @Column(name = "authentication_id")
    var authenticationID: UUID,

    @Column(name = "auth_status")
    var authStatus: String? = AuthStatus.ACTIVE,
)
