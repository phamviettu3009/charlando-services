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
    var fullName: String? = ""
)

fun UserEntity.asUserDTO(): UserDTO {
    return UserDTO(
        id = id,
        fullName = fullName
    )
}


