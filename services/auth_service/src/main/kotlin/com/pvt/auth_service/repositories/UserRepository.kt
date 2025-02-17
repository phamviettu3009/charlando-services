package com.pvt.auth_service.repositories

import com.pvt.auth_service.models.entitys.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository: JpaRepository<UserEntity, UUID> {
}