package com.pvt.auth_service.models.entitys

import com.pvt.auth_service.models.dtos.UserDTO
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Table(name = "auth_user")
@Entity
data class UserEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "full_name")
    var fullName: String? = "",

    @Column(name = "gender")
    var gender: String? = null,

    @Column(name = "date_of_birth")
    var dateOfBirth: Date? = null,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "avatar")
    var avatar: String? = null,

    @Column(name = "online")
    var online: Boolean? = false,
)

fun UserEntity.asUserDTO(): UserDTO {
    return UserDTO(
        id = id,
        fullName = fullName,
        gender = gender,
        dateOfBirth = dateOfBirth,
        phone = phone,
        avatar = avatar,
        online = online
    )
}


