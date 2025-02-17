package com.pvt.channel_service.services

import com.pvt.channel_service.models.dtos.ListRequestDTO
import com.pvt.channel_service.models.dtos.ListResponseDTO
import com.pvt.channel_service.models.dtos.RequestDTO
import com.pvt.channel_service.models.dtos.UserResponseDTO
import java.util.UUID

interface FriendService {
    fun sendRequestAddFriend(request: RequestDTO<Unit>): Any
    fun unFriend(request: RequestDTO<Unit>): Any
    fun confirmationAddFriend(request: RequestDTO<Unit>): Any
    fun cancelRequestAddFriend(request: RequestDTO<Unit>): Any
    fun rejectFriendRequest(request: RequestDTO<Unit>): Any
    fun getFriends(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO>
    fun getFriendsOutsideChannel(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO>
    fun getListRequestAddFriend(request: RequestDTO<ListRequestDTO>): ListResponseDTO<UserResponseDTO>
    fun getNumberRequestAddFriend(request: RequestDTO<Unit>): Long
    fun getFriendStatus(request: RequestDTO<UUID>): Any
}