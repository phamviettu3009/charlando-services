package com.pvt.auth_service.services

import com.pvt.auth_service.models.entitys.UserEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserService {
    fun createUser(user: UserEntity): UserEntity
    fun putUserMessageToRabbit(id: UUID, routing: String, callBackQueue: String)
}