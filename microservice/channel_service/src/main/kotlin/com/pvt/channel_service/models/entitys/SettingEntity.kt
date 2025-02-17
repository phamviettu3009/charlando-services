package com.pvt.channel_service.models.entitys

import com.pvt.channel_service.models.dtos.SettingDTO
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "setting")
@Entity
data class SettingEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "user_id")
    var userID: UUID,

    @Column(name = "public_email")
    var publicEmail: Boolean,

    @Column(name = "public_gender")
    var publicGender: Boolean,

    @Column(name = "public_phone")
    var publicPhone: Boolean,

    @Column(name = "public_dob")
    var publicDob: Boolean,
) {
    fun asSettingDTO(): SettingDTO {
        return SettingDTO(
            publicDob = publicDob,
            publicPhone = publicPhone,
            publicGender = publicGender,
            publicEmail = publicEmail
        )
    }
}