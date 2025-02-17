package com.pvt.channel_service.models.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.pvt.channel_service.models.entitys.UserEntity
import java.util.*

data class UserDTO(
    @JsonProperty("id") var id: UUID,
    @JsonProperty("fullName") var fullName: String,
    @JsonProperty("gender") var gender: String?,
    @JsonProperty("dateOfBirth") var dateOfBirth: Date?,
    @JsonProperty("phone") var phone: String?,
    @JsonProperty("avatar") var avatar: String?,
    @JsonProperty("online") var online: Boolean,
    @JsonProperty("cover_photo") var coverPhoto: String?
) {
    fun asUserEntity(): UserEntity {
        return UserEntity(
            id = id,
            fullName = fullName,
            gender = gender,
            dob = dateOfBirth,
            phone = phone,
            email = "",
            avatar = avatar,
            coverPhoto = coverPhoto
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
}
