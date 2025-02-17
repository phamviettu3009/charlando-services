package com.pvt.channel_service.models.entitys

import com.pvt.channel_service.constants.AuthStatus
import com.pvt.channel_service.models.dtos.*
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

    @Column(name = "dob")
    var dob: Date? = null,

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

    @Column(name = "cover_photo")
    var coverPhoto: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "country_code")
    var countryCode: String? = null,

    @Column(name = "language_code")
    var languageCode: String? = null,
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
            online = online,
            coverPhoto = coverPhoto
        )
    }

    fun asExpandUserResponseDTO2(relationshipStatus: String?, friend: Int, channelID: UUID?, settingDTO: SettingDTO): ExpandUserResponseDTO2 {
        return ExpandUserResponseDTO2(
            id = id,
            fullName = fullName,
            avatar = avatar,
            online = online,
            relationshipStatus = relationshipStatus,
            friend = friend,
            channelID = channelID,
            coverPhoto = coverPhoto,
            gender = if(settingDTO.publicGender) gender else null,
            dob = if(settingDTO.publicDob) dob else null,
            phone = if(settingDTO.publicPhone) phone else null,
            email = if(settingDTO.publicEmail) email else null,
            countryCode = if(settingDTO.publicPhone) countryCode else null,
            languageCode = if(settingDTO.publicPhone) languageCode else null,
            description = description
        )
    }

    fun asExpandUserResponseDTO(settingDTO: SettingDTO): ExpandUserResponseDTO {
        return ExpandUserResponseDTO(
            id = id,
            fullName = fullName,
            avatar = avatar,
            online = online,
            coverPhoto = coverPhoto,
            gender = gender,
            dob = dob,
            phone = phone,
            email = email,
            description = description,
            countryCode = countryCode,
            languageCode = languageCode,
            setting = settingDTO,
        )
    }
}
