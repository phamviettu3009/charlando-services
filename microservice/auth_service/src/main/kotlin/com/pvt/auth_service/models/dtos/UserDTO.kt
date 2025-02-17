package com.pvt.auth_service.models.dtos

import java.util.*

data class UserDTO(
    var id: UUID,
    var fullName: String?
)
