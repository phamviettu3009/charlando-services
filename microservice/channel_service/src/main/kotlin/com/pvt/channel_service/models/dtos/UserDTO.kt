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
) {
    fun asUserEntity(): UserEntity {
        return UserEntity(
            id = id,
            fullName = fullName,
            gender = gender,
            dateOfBirth = dateOfBirth,
            phone = phone,
            email = "",
            avatar = avatar,
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
}
