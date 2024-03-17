package com.pvt.channel_service.models.entitys

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.models.dtos.UserDTO
import com.pvt.channel_service.models.dtos.UserInfoResponseDTO
import com.pvt.channel_service.models.dtos.UserResponseDTO
import java.util.*
import javax.persistence.*

@Table(name = "user_info")
@Entity
data class UserEntity(
    @Id
    var id: UUID,

    @Column(name = "full_name")
    var fullName: String? = "",

    @Column(name = "gender")
    var gender: String? = null,

    @Column(name = "date_of_birth")
    var dateOfBirth: Date? = null,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "avatar")
    var avatar: String? = null,

    @Column(name = "online")
    var online: Boolean = false,

    @Column(name = "auth_status")
    var authStatus: String = AuthStatus.ACTIVE,
) {
    fun toNewRecordMemberEntity(channelID: UUID): MemberEntity {
        return MemberEntity(
            userID = id,
            channelID = channelID
        )
    }

    fun asUserResponseDTO(): UserResponseDTO {
        return UserResponseDTO(
            id = id,
            fullName = fullName,
            avatar = avatar,
            online = online
        )
    }

    fun asUserInfoResponseDTO(relationshipStatus: String?, friend: Int, channelID: UUID?): UserInfoResponseDTO {
        return UserInfoResponseDTO(
            id = id,
            fullName = fullName,
            avatar = avatar,
            online = online,
            relationshipStatus = relationshipStatus,
            friend = friend,
            channelID = channelID
        )
    }
}
