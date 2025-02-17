package com.pvt.channel_service.services

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.UserEntity
import java.util.*

interface UserService {
    fun findCMNUserByID(userID: UUID): UserDTO?
    fun findAllByIDs(userIDs: List<UUID>): List<UserEntity>
    fun getUsers(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO>
    fun getUser(request: RequestDTO<Unit>): ExpandUserResponseDTO
    fun getUser(request: RequestDTO<UUID>): ExpandUserResponseDTO2
    fun updateUser(request: RequestDTO<UserUpdateRequestDTO>): ExpandUserResponseDTO
    fun getUserByID(userID: UUID): UserResponseDTO
    fun updateSetting(request: RequestDTO<SettingDTO>): SettingDTO
}