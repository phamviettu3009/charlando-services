package com.pvt.channel_service.models.dtos

import java.util.*

interface MemberResponseDTO {
    var id: UUID
    var fullName: String?
    var avatar: String?
    var online: Boolean
    var role: String
}