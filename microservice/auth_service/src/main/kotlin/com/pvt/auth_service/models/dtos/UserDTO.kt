package com.pvt.auth_service.models.dtos

import java.util.*

data class UserDTO(
    var id: UUID,
    var fullName: String?,
    var gender: String? = null,
    var dateOfBirth: Date? = null,
    var phone: String? = null,
    var avatar: String? = null,
    var online: Boolean? = false,
)
