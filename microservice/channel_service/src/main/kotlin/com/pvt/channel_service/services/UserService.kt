package com.pvt.channel_service.services

import com.pvt.channel_service.models.dtos.*
import com.pvt.channel_service.models.entitys.UserEntity
import java.util.*

interface UserService {
    fun findCMNUserByID(userID: UUID): UserDTO?
    fun findAllByIDs(userIDs: List<UUID>): List<UserEntity>
    fun getUsers(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO>
    fun getUser(request: RequestDTO<Unit>): UserResponseDTO
    fun getUsersInChannel(request: RequestDTO<ListRequestDTO>): ListResponseDTO<MemberResponseDTO>
    fun getUserInfo(request: RequestDTO<Unit>): UserInfoResponseDTO
}